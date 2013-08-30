package org.springframework.social.yammer.api.impl;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.client.match.RequestMatchers.method;
import static org.springframework.test.web.client.match.RequestMatchers.requestTo;
import static org.springframework.test.web.client.response.ResponseCreators.withStatus;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.social.NotAuthorizedException;
import org.springframework.social.RateLimitExceededException;

public class ApiErrorTest extends AbstractYammerApiTest {

    /**
     * Tests that 401 is interpreted as rate limit exceeded if json body returned indicates so. According to Yammer API doc 403 will be returned if rate limit
     * is exceeded, empiric evidence suggests otherwise.
     * 
     * @throws IOException
     */
    //Ixxus: relies on HttpStatus.getReasonPhrase (Spring 3.1 > required)
    @Ignore
    @Test(expected = RateLimitExceededException.class)
    public void testRateLimitExceeded() throws IOException {
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        mockServer.expect(requestTo("https://www.yammer.com/api/v1/users.json?page=1&reverse=false"))
                .andExpect(method(GET))
                .andRespond(withStatus(UNAUTHORIZED).body(jsonResource("testdata/rate-limit-error")).contentType(MediaType.APPLICATION_JSON));
        yammerTemplate.userOperations().getUsers(1);
    }

    //Ixxus: relies on HttpStatus.getReasonPhrase (Spring 3.1 > required)
    @Ignore
    @Test(expected = RateLimitExceededException.class)
    public void testRateLimitExceeded_whenForbidden() throws IOException {
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        mockServer.expect(requestTo("https://www.yammer.com/api/v1/users.json?page=1&reverse=false"))
                .andExpect(method(GET))
                .andRespond(withStatus(FORBIDDEN));
        yammerTemplate.userOperations().getUsers(1);
    }

    //Ixxus: relies on HttpStatus.getReasonPhrase (Spring 3.1 > required)
    @Ignore
    @Test(expected = NotAuthorizedException.class)
    public void testNotAutorizedException() {
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        mockServer.expect(requestTo("https://www.yammer.com/api/v1/users.json?page=1&reverse=false"))
                .andExpect(method(GET))
                .andRespond(withStatus(UNAUTHORIZED));
        yammerTemplate.userOperations().getUsers(1);
    }
}
