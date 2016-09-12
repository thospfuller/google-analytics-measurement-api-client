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
    	queryBuilder.checkSizeOf("foo", "foobar", 7);
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testCheckSizeOfShouldThrowAnException() {
    	queryBuilder.checkSizeOf("foo", "foobar", 3);
    }

    static String generateStringOfSize(int byteSize) {

    	StringBuffer buffer = new StringBuffer(byteSize);

    	for (int ctr = 0; ctr < byteSize; ctr++)
    		buffer.append("X");

    	return buffer.toString();
    }

    @Test
    public void testWithAid() {

        queryBuilder.withAid("123");

        assertEquals("http://www.google-analytics.com/collect?aid=123", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithAidSizeOOB() {
        queryBuilder.withAid(generateStringOfSize(151));
    }

    @Test
    public void testWithAiid() {

        queryBuilder.withAiid("123");

        assertEquals("http://www.google-analytics.com/collect?aiid=123", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithAiidSizeOOB() {
        queryBuilder.withAiid(generateStringOfSize(151));
    }

    @Test
    public void testWithAip() {

        queryBuilder.withAip(true);

        assertEquals("http://www.google-analytics.com/collect?aip=1", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithV() {

        queryBuilder.withV("123");

        assertEquals("http://www.google-analytics.com/collect?v=123", queryBuilder.getEscapedURI());
    }
}
