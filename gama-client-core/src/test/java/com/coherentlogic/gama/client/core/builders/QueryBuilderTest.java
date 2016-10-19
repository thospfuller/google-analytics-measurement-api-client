package com.coherentlogic.gama.client.core.builders;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.coherentlogic.gama.client.core.exceptions.InvalidQueueTime;
import com.coherentlogic.gama.client.core.exceptions.MaxLengthInBytesExceededException;
import com.coherentlogic.gama.client.core.exceptions.ValueOutOfBoundsException;

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
        queryBuilder = new QueryBuilder((RestTemplate) null);
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
    public void testAssertBetweenOK() {
        queryBuilder.assertBetween(FOO, 1, 10, 5);
    }

    @Test(expected=ValueOutOfBoundsException.class)
    public void testAssertBetweenTooLow() {
        queryBuilder.assertBetween(FOO, 1, 10, 0);
    }

    @Test(expected=ValueOutOfBoundsException.class)
    public void testAssertBetweenTooHigh() {
        queryBuilder.assertBetween(FOO, 1, 10, 11);
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

    @Test
    public void testWithTid() {

        queryBuilder.withTid(FOO);

        assertEquals("http://www.google-analytics.com/collect?tid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithDs() {

        queryBuilder.withDs(FOO);

        assertEquals("http://www.google-analytics.com/collect?ds=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=InvalidQueueTime.class)
    public void testWithQtUsingNegativeTime() {
        queryBuilder.withQt(-1234L);
    }

    @Test(expected=InvalidQueueTime.class)
    public void testWithQtUsingZero() {
        queryBuilder.withQt(0L);
    }

    @Test
    public void testWithQt() {

        queryBuilder.withQt(1234L);

        assertEquals("http://www.google-analytics.com/collect?qt=1234", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithZ() {

        queryBuilder.withZ(FOO);

        assertEquals("http://www.google-analytics.com/collect?z=foo", queryBuilder.getEscapedURI());
    }

    /**
     * Note we're not testing the withCIDAsRandomUUID method.
     */
    @Test
    public void testWithCID() {

        queryBuilder.withCID(FOO);

        assertEquals("http://www.google-analytics.com/collect?cid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUID() {

        queryBuilder.withUID(FOO);

        assertEquals("http://www.google-analytics.com/collect?uid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithSc() {

        queryBuilder.withSc(FOO);

        assertEquals("http://www.google-analytics.com/collect?sc=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=NullPointerException.class)
    public void testWithUipUsingANullInetAddress() throws UnknownHostException {

        InetAddress inetAddress = null;

        queryBuilder.withUip(inetAddress);
    }

    @Test
    public void testWithUipUsingAnInetAddress() throws UnknownHostException {

        InetAddress inetAddress = InetAddress.getLocalHost();

        queryBuilder.withUip(inetAddress);

        // Subject to change so if this test is failing, check the IP address and change the string below.
        assertEquals("http://www.google-analytics.com/collect?uip=192.168.1.156", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUip() {

        queryBuilder.withUip(FOO);

        assertEquals("http://www.google-analytics.com/collect?uip=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithUa() {

        queryBuilder.withUa(FOO);

        assertEquals("http://www.google-analytics.com/collect?ua=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithGeoID() {

        queryBuilder.withGeoID(FOO);

        assertEquals("http://www.google-analytics.com/collect?geoid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithDr() {

        queryBuilder.withDr(FOO);

        assertEquals("http://www.google-analytics.com/collect?dr=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithDrThatExceedsAllowedSize() {
        queryBuilder.withDr(generateStringOfSize(2049));
    }

    @Test
    public void testWithCn() {

        queryBuilder.withCn(FOO);

        assertEquals("http://www.google-analytics.com/collect?cn=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCnThatExceedsAllowedSize() {
        queryBuilder.withCn(generateStringOfSize(101));
    }

    @Test
    public void testWithCs() {

        queryBuilder.withCs(FOO);

        assertEquals("http://www.google-analytics.com/collect?cs=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithCm() {

        queryBuilder.withCm(FOO);

        assertEquals("http://www.google-analytics.com/collect?cm=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCmThatExceedsAllowedSize() {
        queryBuilder.withCm(generateStringOfSize(51));
    }

    @Test
    public void testWithCk() {

        queryBuilder.withCk(FOO);

        assertEquals("http://www.google-analytics.com/collect?ck=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCkThatExceedsAllowedSize() {
        queryBuilder.withCk(generateStringOfSize(501));
    }

    @Test
    public void testWithCc() {

        queryBuilder.withCc(FOO);

        assertEquals("http://www.google-analytics.com/collect?cc=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCcThatExceedsAllowedSize() {
        queryBuilder.withCc(generateStringOfSize(501));
    }

    @Test
    public void testWithCi() {

        queryBuilder.withCi(FOO);

        assertEquals("http://www.google-analytics.com/collect?ci=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCiThatExceedsAllowedSize() {
        queryBuilder.withCi(generateStringOfSize(101));
    }

    @Test
    public void testWithGclid() {

        queryBuilder.withGclid(FOO);

        assertEquals("http://www.google-analytics.com/collect?gclid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithDclid() {

        queryBuilder.withDclid(FOO);

        assertEquals("http://www.google-analytics.com/collect?dclid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithSr() {

        queryBuilder.withSr(FOO);

        assertEquals("http://www.google-analytics.com/collect?sr=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithSrThatExceedsAllowedSize() {
        queryBuilder.withSr(generateStringOfSize(21));
    }

    @Test
    public void testWithVp() {

        queryBuilder.withVp(FOO);

        assertEquals("http://www.google-analytics.com/collect?vp=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithVpThatExceedsAllowedSize() {
        queryBuilder.withVp(generateStringOfSize(21));
    }

    @Test
    public void testWithDe() {

        queryBuilder.withDe(FOO);

        assertEquals("http://www.google-analytics.com/collect?de=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithDeThatExceedsAllowedSize() {
        queryBuilder.withDe(generateStringOfSize(21));
    }

    @Test
    public void testWithSd() {

        queryBuilder.withSd(FOO);

        assertEquals("http://www.google-analytics.com/collect?sd=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithSdThatExceedsAllowedSize() {
        queryBuilder.withSd(generateStringOfSize(21));
    }

    @Test
    public void testWithUl() {

        queryBuilder.withUl(FOO);

        assertEquals("http://www.google-analytics.com/collect?ul=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithUlThatExceedsAllowedSize() {
        queryBuilder.withUl(generateStringOfSize(21));
    }

    @Test
    public void testWithJe() {

        queryBuilder.withJe(true);

        assertEquals("http://www.google-analytics.com/collect?je=1", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithFl() {

        queryBuilder.withFl(FOO);

        assertEquals("http://www.google-analytics.com/collect?fl=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithFlThatExceedsAllowedSize() {
        queryBuilder.withFl(generateStringOfSize(21));
    }

    /**
     * Note we're not testing the withTAsX methods.
     */
    @Test
    public void testWithT() {

        queryBuilder.withT(FOO);

        assertEquals("http://www.google-analytics.com/collect?t=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithNi() {

        queryBuilder.withNi(true);

        assertEquals("http://www.google-analytics.com/collect?ni=1", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithDl() {

        queryBuilder.withDl(FOO);

        assertEquals("http://www.google-analytics.com/collect?dl=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithDlThatExceedsAllowedSize() {
        queryBuilder.withDl(generateStringOfSize(2049));
    }

    @Test
    public void testWithDh() {

        queryBuilder.withDh(FOO);

        assertEquals("http://www.google-analytics.com/collect?dh=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithDhThatExceedsAllowedSize() {
        queryBuilder.withDh(generateStringOfSize(101));
    }

    @Test
    public void testWithDp() {

        queryBuilder.withDp(FOO);

        assertEquals("http://www.google-analytics.com/collect?dp=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithDpThatExceedsAllowedSize() {
        queryBuilder.withDp(generateStringOfSize(2049));
    }

    @Test
    public void testWithDt() {

        queryBuilder.withDt(FOO);

        assertEquals("http://www.google-analytics.com/collect?dt=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithDtThatExceedsAllowedSize() {
        queryBuilder.withDt(generateStringOfSize(1501));
    }

    @Test
    public void testWithCd() {

        queryBuilder.withCd(FOO);

        assertEquals("http://www.google-analytics.com/collect?cd=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCdThatExceedsAllowedSize() {
        queryBuilder.withCd(generateStringOfSize(2049));
    }

    @Test
    public void testWithLinkid() {

        queryBuilder.withLinkid(FOO);

        assertEquals("http://www.google-analytics.com/collect?linkid=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithAn() {

        queryBuilder.withAn(FOO);

        assertEquals("http://www.google-analytics.com/collect?an=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithAnThatExceedsAllowedSize() {
        queryBuilder.withAn(generateStringOfSize(101));
    }

    @Test
    public void testWithAv() {

        queryBuilder.withAv(FOO);

        assertEquals("http://www.google-analytics.com/collect?av=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithAvThatExceedsAllowedSize() {
        queryBuilder.withAv(generateStringOfSize(101));
    }

    @Test
    public void testWithEc() {

        queryBuilder.withEc(FOO);

        assertEquals("http://www.google-analytics.com/collect?ec=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithEcThatExceedsAllowedSize() {
        queryBuilder.withEc(generateStringOfSize(151));
    }

    @Test
    public void testWithEa() {

        queryBuilder.withEa(FOO);

        assertEquals("http://www.google-analytics.com/collect?ea=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithElThatExceedsAllowedSize() {
        queryBuilder.withEl(generateStringOfSize(501));
    }

    @Test
    public void testWithEl() {

        queryBuilder.withEl(FOO);

        assertEquals("http://www.google-analytics.com/collect?el=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithEv() {

        queryBuilder.withEv(123);

        assertEquals("http://www.google-analytics.com/collect?ev=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithTi() {

        queryBuilder.withTi(FOO);

        assertEquals("http://www.google-analytics.com/collect?ti=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithTiThatExceedsAllowedSize() {
        queryBuilder.withTi(generateStringOfSize(501));
    }

    @Test
    public void testWithTa() {

        queryBuilder.withTa(FOO);

        assertEquals("http://www.google-analytics.com/collect?ta=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithTaThatExceedsAllowedSize() {
        queryBuilder.withTa(generateStringOfSize(501));
    }

    @Test
    public void testWithTt() {

        queryBuilder.withTt(new BigDecimal ("123.45"));

        assertEquals("http://www.google-analytics.com/collect?tt=123.45", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIn() {

        queryBuilder.withIn(FOO);

        assertEquals("http://www.google-analytics.com/collect?in=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithInThatExceedsAllowedSize() {
        queryBuilder.withIn(generateStringOfSize(501));
    }

    @Test
    public void testWithIpAsString() {

        queryBuilder.withIp(FOO);

        assertEquals("http://www.google-analytics.com/collect?ip=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIpAsBigDecimal() {

        queryBuilder.withIp(new BigDecimal ("123.45"));

        assertEquals("http://www.google-analytics.com/collect?ip=123.45", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIqAsString() {

        queryBuilder.withIq(FOO);

        assertEquals("http://www.google-analytics.com/collect?iq=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIqAsInt() {

        queryBuilder.withIq(123);

        assertEquals("http://www.google-analytics.com/collect?iq=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIc() {

        queryBuilder.withIc(FOO);

        assertEquals("http://www.google-analytics.com/collect?ic=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithIcThatExceedsAllowedSize() {
        queryBuilder.withIc(generateStringOfSize(501));
    }

    @Test
    public void testWithIv() {

        queryBuilder.withIv(FOO);

        assertEquals("http://www.google-analytics.com/collect?iv=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithIvThatExceedsAllowedSize() {
        queryBuilder.withIv(generateStringOfSize(501));
    }

    @Test
    public void testWithCu() {

        queryBuilder.withCu(FOO);

        assertEquals("http://www.google-analytics.com/collect?cu=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithCuThatExceedsAllowedSize() {
        queryBuilder.withCu(generateStringOfSize(501));
    }

    @Test
    public void testWithPrNId() {

        queryBuilder.withPrNId(100, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr100id=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithPrNIdThatExceedsAllowedSize() {
        queryBuilder.withPrNId(123, generateStringOfSize(501));
    }

    @Test
    public void testWithPrNNm() {

        queryBuilder.withPrNNm(100, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr100nm=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithPrNNmThatExceedsAllowedSize() {
        queryBuilder.withPrNNm(123, generateStringOfSize(501));
    }

    @Test
    public void testWithPrNBr() {

        queryBuilder.withPrNBr(100, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr100br=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithPrNBrThatExceedsAllowedSize() {
        queryBuilder.withPrNBr(123, generateStringOfSize(501));
    }

    @Test
    public void testWithTrAsString() {

        queryBuilder.withTr(FOO);

        assertEquals("http://www.google-analytics.com/collect?tr=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithTrAsBigDecimal() {

        queryBuilder.withTr(new BigDecimal ("123.45"));

        assertEquals("http://www.google-analytics.com/collect?tr=123.45", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNCaWithString() {

        queryBuilder.withPrNCa(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr123ca=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithPrNCaThatExceedsAllowedSize() {
        queryBuilder.withPrNCa(123, generateStringOfSize(501));
    }

    @Test
    public void testWithPrNVaWithString() {

        queryBuilder.withPrNVa(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr123va=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithPrNVaThatExceedsAllowedSize() {
        queryBuilder.withPrNVa(123, generateStringOfSize(501));
    }

    @Test
    public void testWithPrNPrWithString() {

        queryBuilder.withPrNPr(123, "456.78");

        assertEquals("http://www.google-analytics.com/collect?pr123pr=456.78", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNPrWithInteger() {

        queryBuilder.withPrNPr(123, new BigDecimal ("456.78"));

        assertEquals("http://www.google-analytics.com/collect?pr123pr=456.78", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNQtWithString() {

        queryBuilder.withPrNQt(123, "456");

        assertEquals("http://www.google-analytics.com/collect?pr123qt=456", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNQtWithInteger() {

        queryBuilder.withPrNQt(123, 456);

        assertEquals("http://www.google-analytics.com/collect?pr123qt=456", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNCcWithString() {

        queryBuilder.withPrNCc(123, "456");

        assertEquals("http://www.google-analytics.com/collect?pr123cc=456", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithPrNCcThatExceedsAllowedSize() {
        queryBuilder.withPrNCc(123, generateStringOfSize(501));
    }

    @Test
    public void testWithPrNCcWithInteger() {

        queryBuilder.withPrNCc(123, 456);

        assertEquals("http://www.google-analytics.com/collect?pr123cc=456", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNPsWithString() {

        queryBuilder.withPrNPs(123, "456");

        assertEquals("http://www.google-analytics.com/collect?pr123ps=456", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrNPsWithInteger() {

        queryBuilder.withPrNPs(123, 456);

        assertEquals("http://www.google-analytics.com/collect?pr123ps=456", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrXCdY() {

        queryBuilder.withPrXCdY(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr123cd124=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPrXCmY() {

        queryBuilder.withPrXCmY(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?pr123cm124=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPa() {

        queryBuilder.withPa(FOO);

        assertEquals("http://www.google-analytics.com/collect?pa=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsAdd() {

        queryBuilder.withPaAsAdd();

        assertEquals("http://www.google-analytics.com/collect?pa=add", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsCheckout() {

        queryBuilder.withPaAsCheckout();

        assertEquals("http://www.google-analytics.com/collect?pa=checkout", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsCheckoutOption() {

        queryBuilder.withPaAsCheckoutOption();

        assertEquals("http://www.google-analytics.com/collect?pa=checkout_option", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsClick() {

        queryBuilder.withPaAsClick();

        assertEquals("http://www.google-analytics.com/collect?pa=click", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsDetail() {

        queryBuilder.withPaAsDetail();

        assertEquals("http://www.google-analytics.com/collect?pa=detail", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsPurchase() {

        queryBuilder.withPaAsPurchase();

        assertEquals("http://www.google-analytics.com/collect?pa=purchase", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsRefund() {

        queryBuilder.withPaAsRefund();

        assertEquals("http://www.google-analytics.com/collect?pa=refund", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPaAsRemove() {

        queryBuilder.withPaAsRemove();

        assertEquals("http://www.google-analytics.com/collect?pa=remove", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithTsWithBigDecimal() {

        queryBuilder.withTs(new BigDecimal ("123.45"));

        assertEquals("http://www.google-analytics.com/collect?ts=123.45", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithTsWithString() {

        queryBuilder.withTs("123.45");

        assertEquals("http://www.google-analytics.com/collect?ts=123.45", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithTcc() {

        queryBuilder.withTcc(FOO);

        assertEquals("http://www.google-analytics.com/collect?tcc=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithPal() {

        queryBuilder.withPal(FOO);

        assertEquals("http://www.google-analytics.com/collect?pal=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithCos() {

        queryBuilder.withCos(123);

        assertEquals("http://www.google-analytics.com/collect?cos=123", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithCol() {

        queryBuilder.withCol(FOO);

        assertEquals("http://www.google-analytics.com/collect?col=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXNm() {

        queryBuilder.withIlXNm(123, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123nm=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYId() {

        queryBuilder.withIlXPiYId(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124id=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYNm() {

        queryBuilder.withIlXPiYNm(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124nm=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYBr() {

        queryBuilder.withIlXPiYBr(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124br=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYCa() {

        queryBuilder.withIlXPiYCa(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124ca=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYVa() {

        queryBuilder.withIlXPiYVa(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124va=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYPs() {

        queryBuilder.withIlXPiYPs(123, 124, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124ps=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYCdZ() {

        queryBuilder.withIlXPiYCdZ(123, 124, 125, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124cd125=foo", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithIlXPiYCmZ() {

        queryBuilder.withIlXPiYCmZ(123, 124, 125, FOO);

        assertEquals("http://www.google-analytics.com/collect?il123pi124cm125=foo", queryBuilder.getEscapedURI());
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

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithSnThatExceedsAllowedSize() {
        queryBuilder.withSn(generateStringOfSize(51));
    }

    @Test
    public void testWithSa() {

        queryBuilder.withSa(FOO);

        assertEquals("http://www.google-analytics.com/collect?sa=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithSaThatExceedsAllowedSize() {
        queryBuilder.withSa(generateStringOfSize(51));
    }

    @Test
    public void testWithSt() {

        queryBuilder.withSt(FOO);

        assertEquals("http://www.google-analytics.com/collect?st=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithStThatExceedsAllowedSize() {
        queryBuilder.withSt(generateStringOfSize(2049));
    }

    @Test
    public void testWithUtc() {

        queryBuilder.withUtc(FOO);

        assertEquals("http://www.google-analytics.com/collect?utc=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithUtcThatExceedsAllowedSize() {
        queryBuilder.withUtc(generateStringOfSize(151));
    }

    @Test
    public void testWithUtv() {

        queryBuilder.withUtv(FOO);

        assertEquals("http://www.google-analytics.com/collect?utv=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithUtvThatExceedsAllowedSize() {
        queryBuilder.withUtv(generateStringOfSize(501));
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

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithUtlThatExceedsAllowedSize() {
        queryBuilder.withUtl(generateStringOfSize(501));
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

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithExdThatExceedsAllowedSize() {
        queryBuilder.withExd(generateStringOfSize(151));
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

    @Test(expected=ValueOutOfBoundsException.class)
    public void testWithCdXOutOfBounds() {
        queryBuilder.withCdX(201, FOO);
    }
//dd
    @Test
    public void testWithCmX() {

        queryBuilder.withCmX(100, 200);

        assertEquals("http://www.google-analytics.com/collect?cm100=200", queryBuilder.getEscapedURI());
    }

    @Test
    public void testWithXid() {

        queryBuilder.withXid(FOO);

        assertEquals("http://www.google-analytics.com/collect?xid=foo", queryBuilder.getEscapedURI());
    }

    @Test(expected=MaxLengthInBytesExceededException.class)
    public void testWithXidThatExceedsAllowedSize() {
        queryBuilder.withXid(generateStringOfSize(41));
    }

    @Test
    public void testWithXvar() {

        queryBuilder.withXvar(FOO);

        assertEquals("http://www.google-analytics.com/collect?xvar=foo", queryBuilder.getEscapedURI());
    }
}
