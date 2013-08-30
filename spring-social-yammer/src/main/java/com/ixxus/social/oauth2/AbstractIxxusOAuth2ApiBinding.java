package com.ixxus.social.oauth2;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.OAuth2Version;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

/**
 * This class has been created to allow the forked version of spring-social-yammer to work with the Alfresco version of spring-social-core (1.0.0.RC1).
 * 
 * Within this verison the org.springframework.social.oauth2.OAuth2Version returns a capitalised String BEARER which causes a 401 against the Yammer API which
 * expected the string to be Bearer
 * 
 * This class allows the correctly capitalised string to be returned
 * 
 * @author Simon Hutchinson
 * 
 */
public abstract class AbstractIxxusOAuth2ApiBinding extends AbstractOAuth2ApiBinding {
    private final String accessToken;

    private final RestTemplate restTemplate;

    /**
     * Constructs the API template without user authorization. This is useful for accessing operations on a provider's API that do not require user
     * authorization.
     */
    protected AbstractIxxusOAuth2ApiBinding() {
        accessToken = null;
        restTemplate = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
        restTemplate.setMessageConverters(getMessageConverters());
    }

    /**
     * Constructs the API template with OAuth credentials necessary to perform operations on behalf of a user.
     * 
     * @param accessToken
     *            the access token
     */
    protected AbstractIxxusOAuth2ApiBinding(String accessToken) {
        this.accessToken = accessToken;
        restTemplate = createRestTemplate(accessToken);
        restTemplate.setMessageConverters(getMessageConverters());
    }

    /**
     * Added by Ixxus
     * 
     * @return
     */
    private RestTemplate createRestTemplate(String accessToken) {
        RestTemplate client = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
        client.setRequestFactory(new IxxusSpring30OAuth2RequestFactory(client.getRequestFactory(), accessToken, getOAuth2Version()));
        return client;
    }

    /**
     * Set the ClientHttpRequestFactory. This is useful when custom configuration of the request factory is required, such as configuring custom SSL details.
     * 
     * @param requestFactory
     *            the request factory
     */
    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        if (isAuthorized()) {
            restTemplate.setRequestFactory(new IxxusSpring30OAuth2RequestFactory(requestFactory, accessToken, getOAuth2Version()));
        } else {
            restTemplate.setRequestFactory(requestFactory);
        }
    }

    // implementing ApiBinding

    public boolean isAuthorized() {
        return accessToken != null;
    }

    // public implementation operations

    /**
     * Obtains a reference to the REST client backing this API binding and used to perform API calls. Callers may use the RestTemplate to invoke other API
     * operations not yet modeled by the binding interface. Callers may also modify the configuration of the RestTemplate to support unit testing the API
     * binding with a mock server in a test environment. During construction, subclasses may apply customizations to the RestTemplate needed to invoke a
     * specific API.
     * 
     * @see RestTemplate#setMessageConverters(java.util.List)
     * @see RestTemplate#setErrorHandler(org.springframework.web.client.ResponseErrorHandler)
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    // subclassing hooks

    /**
     * Returns the version of OAuth2 the API implements. By default, returns {@link OAuth2Version#BEARER} indicating versions of OAuth2 that apply the bearer
     * token scheme. Subclasses may override to return another version.
     * 
     * @see OAuth2Version
     */
    protected OAuth2Version getOAuth2Version() {
        return OAuth2Version.BEARER;
    }

    /**
     * Returns a list of {@link HttpMessageConverter}s to be used by the internal {@link RestTemplate}. By default, this includes a
     * {@link StringHttpMessageConverter}, a {@link MappingJacksonHttpMessageConverter}, and a {@link FormHttpMessageConverter}. The
     * {@link FormHttpMessageConverter} is set to use "UTF-8" character encoding. Override this method to add additional message converters or to replace the
     * default list of message converters.
     */
    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.setCharset(Charset.forName("UTF-8"));
        messageConverters.add(formHttpMessageConverter);
        messageConverters.add(new MappingJacksonHttpMessageConverter());
        return messageConverters;
    }
}