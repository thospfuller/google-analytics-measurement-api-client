package com.coherentlogic.gama.client.core.builders;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coherentlogic.gama.client.core.exceptions.MaxLengthInBytesExceededException;

/**
 * Unit test for the {@link QueryBuilder} class.
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class QueryBuilderTest {

    private QueryBuilder queryBuilder = null;

    @Before
    public void setUp() throws Exception {
        queryBuilder = new QueryBuilder(null);
    }

    @After
    public void tearDown() throws Exception {
        queryBuilder = null;
    }

    @Test
    public void testCheckSizeOf() {
        QueryBuilder.checkSizeOf("foo", "foobar", 7);
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testCheckSizeOfShouldThrowAnException() {
        QueryBuilder.checkSizeOf("foo", "foobar", 3);
    }

    @Test
    public void testWithV() {

        queryBuilder.withV("123");

        assertEquals("http://www.google-analytics.com/collect?v=123", queryBuilder.getEscapedURI());
    }

}
