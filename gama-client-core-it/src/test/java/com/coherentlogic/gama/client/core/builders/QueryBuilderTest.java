package com.coherentlogic.gama.client.core.builders;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.coherentlogic.gama.client.core.exceptions.PostFailedException;

/**
 * Integration test for the {@link QueryBuilder} class.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class QueryBuilderTest {

    public static final String GOOGLE_ANALYTICS_UA_ID = "GOOGLE_ANALYTICS_UA_ID";

    public static final String googleAnalyticsUAID = System.getenv(GOOGLE_ANALYTICS_UA_ID);

    private QueryBuilder queryBuilder = null;

    @Before
    public void setUp() throws Exception {
        queryBuilder = new QueryBuilder();
    }

    @After
    public void tearDown() throws Exception {
        queryBuilder = null;
    }

    @Test
    public void testSuccessfulGAPost () {

        String result = queryBuilder
            .withV1()
            .withTid(googleAnalyticsUAID)
            .withCIDAsRandomUUID()
            .withTAsEvent()
            .withEc ("Integration Test")
            .withAn ("CL GAMA Client")
            .withEa ("Integration Test Started")
            .withAv ("Version 0.8.5-RELEASE")
            .withEl ("Version 0.8.5-RELEASE")
            .doPost();

        // We'll need to manually check that the data has, in fact, been recevied by GA.
    }

    /**
     * This is not an integration test however it does belong here. The Google Analytics Measurement API will accept
     * just about any call made, returning a 200 (OK) status code, even when the data sent is completely wrong. We need
     * to make sure than anything aside from OK results in an exception being thrown.
     */
    @Test(expected=PostFailedException.class)
    public void testUnsuccessfulGAPost () {

        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        ResponseEntity responseEntity = mock (ResponseEntity.class);

        when (
            mockRestTemplate.exchange(
                any(URI.class),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
            )
        ).thenReturn (responseEntity);

        when (responseEntity.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

        String result = new QueryBuilder (mockRestTemplate).doPost();
    }

    
}
