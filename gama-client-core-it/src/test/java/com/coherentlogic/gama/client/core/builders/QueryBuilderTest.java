package com.coherentlogic.gama.client.core.builders;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

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
        queryBuilder = new QueryBuilder(new RestTemplate ());
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

}
