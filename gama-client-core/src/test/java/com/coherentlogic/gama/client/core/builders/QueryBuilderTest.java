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

	public static final String FOO = "foo";

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

    // -----

    @Test
    public void testWithIlXPiYCdZ() {

        queryBuilder.withIlXPiYCdZ(123, 456, 789, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi456cd789=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYCmZ() {

        queryBuilder.withIlXPiYCmZ(123, 456, 789, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi456cm789=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPromoNId() {

        queryBuilder.withPromoNId(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?promo123id=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPromoNNm() {

        queryBuilder.withPromoNNm(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?promo123nm=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPromoNCr() {

        queryBuilder.withPromoNCr(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?promo123cr=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPromoNPs() {

        queryBuilder.withPromoNPs(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?promo123ps=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPromoa() {

        queryBuilder.withPromoa(FOO);

        assertEquals("http://www.google-analytics.com/collect?promoa=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithSn() {

        queryBuilder.withSn(FOO);

        assertEquals("http://www.google-analytics.com/collect?sn=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithSa() {

        queryBuilder.withSa(FOO);

        assertEquals("http://www.google-analytics.com/collect?sa=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithSt() {

        queryBuilder.withSt(FOO);

        assertEquals("http://www.google-analytics.com/collect?st=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUtc() {

        queryBuilder.withUtc(FOO);

        assertEquals("http://www.google-analytics.com/collect?utc=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUtv() {

        queryBuilder.withUtv(FOO);

        assertEquals("http://www.google-analytics.com/collect?utv=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUtt() {

        queryBuilder.withUtt(123L);

        assertEquals("http://www.google-analytics.com/collect?utt=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUtl() {

        queryBuilder.withUtl(FOO);

        assertEquals("http://www.google-analytics.com/collect?utl=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPlt() {

        queryBuilder.withPlt(123L);

        assertEquals("http://www.google-analytics.com/collect?plt=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithDns() {

        queryBuilder.withDns(123L);

        assertEquals("http://www.google-analytics.com/collect?dns=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPdt() {

        queryBuilder.withPdt(123L);

        assertEquals("http://www.google-analytics.com/collect?pdt=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithRrt() {

        queryBuilder.withRrt(123L);

        assertEquals("http://www.google-analytics.com/collect?rrt=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithTcp() {

        queryBuilder.withTcp(123L);

        assertEquals("http://www.google-analytics.com/collect?tcp=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithSrt() {

        queryBuilder.withSrt(123L);

        assertEquals("http://www.google-analytics.com/collect?srt=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithDit() {

        queryBuilder.withDit(123L);

        assertEquals("http://www.google-analytics.com/collect?dit=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithClt() {

        queryBuilder.withClt(123L);

        assertEquals("http://www.google-analytics.com/collect?clt=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithExd() {

        queryBuilder.withExd(FOO);

        assertEquals("http://www.google-analytics.com/collect?exd=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithExf() {

        queryBuilder.withExf(true);

        assertEquals("http://www.google-analytics.com/collect?exf=1", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithCdX() {

        queryBuilder.withCdX(100, FOO);

        assertEquals("http://www.google-analytics.com/collect?cd100=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithCmX() {

        queryBuilder.withCmX(100, 200);

        assertEquals("http://www.google-analytics.com/collect?cm100=200", queryBuilder.getEscapedURI());
    }
}
