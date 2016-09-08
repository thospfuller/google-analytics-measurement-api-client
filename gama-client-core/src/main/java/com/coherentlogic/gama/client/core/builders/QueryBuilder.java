package com.coherentlogic.gama.client.core.builders;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.coherentlogic.coherent.data.model.core.builders.rest.AbstractRESTQueryBuilder;
import com.coherentlogic.coherent.data.model.core.util.Utils;
import com.coherentlogic.coherent.data.model.core.util.WelcomeMessage;
import com.coherentlogic.gama.client.core.exceptions.InvalidQueueTime;
import com.coherentlogic.gama.client.core.exceptions.MaxLengthInBytesExceededException;

/**
 * Class is used to send events to Google Analytics via the Measurement API.
 *
 * @see <a href="https://ga-dev-tools.appspot.com/hit-builder/">Hit Builder</a>
 * @see <a hre="https://developers.google.com/analytics/devguides/collection/protocol/v1/">Measurement Protocol Overview</a>
 * @see <a hre="https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide">Working with the Measurement Protocol</a>
 * @see <a hre="https://developers.google.com/analytics/devguides/collection/protocol/v1/reference">Measurement Protocol Reference</a>
 * @see <a hre="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters">Measurement Protocol Parameter Reference</a>
 *
 * @author <a href="https://www.linkedin.com/in/thomasfuller">Thomas P. Fuller</a>
 * @author <a href="mailto:support@coherentlogic.com">Support</a>
 */
public class QueryBuilder extends AbstractRESTQueryBuilder<String> {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    static final String[] WELCOME_MESSAGE = {
        "*************************************************************************************************************",
        "***                                                                                                       ***",
        "***               Welcome  to  the  Google  Analytics  Measurement  API  Client  for Java                 ***",
        "***                                                                                                       ***",
        "***                                         Version 0.8.5-RELEASE                                         ***",
        "***                                                                                                       ***",
        "***                              Please take a moment to follow us on Twitter:                            ***",
        "***                                                                                                       ***",
        "***                                    www.twitter.com/CoherentMktData                                    ***",
        "***                                                                                                       ***",
        "***                                          or on LinkedIn:                                              ***",
        "***                                                                                                       ***",
        "***                            www.linkedin.com/company/coherent-logic-limited                            ***",
        "***                                                                                                       ***",
        "***                            The project and issue tracker can be found here:                           ***",
        "***                                                                                                       ***",
        "***           https://bitbucket.org/CoherentLogic/coherent-datafeed-thomson-reuters-trep-edition          ***",
        "***                                                                                                       ***",
        "*** ----------------------------------------------------------------------------------------------------- ***",
        "***                                                                                                       ***",
        "*** BE ADVISED:                                                                                           ***",
        "***                                                                                                       ***",
        "*** This framework uses the Google Analytics Measurement API (GAM) to track framework usage  information. ***",
        "*** As this software is open-source, you are welcomed to review our use of GAM -- please  see  the  class ***",
        "*** named  com.coherentlogic.coherent.datafeed.services.GoogleAnalyticsMeasurementService  and  feel free ***",
        "*** to send us an email if you have further questions.                                                    ***",
        "***                                                                                                       ***",
        "*** We do NOT recommend disabling this feature however we offer the option below, just add the following  ***",
        "*** VM parameter and tracking will be disabled:                                                           ***",
        "***                                                                                                       ***",
        "*** -DGOOGLE_ANALYTICS_TRACKING=false                                                                     ***",
        "***                                                                                                       ***",
        "*** ----------------------------------------------------------------------------------------------------- ***",
        "***                                                                                                       ***",
        "*** We offer support and consulting services to businesses that  utilize  this  framework  or  that  have ***",
        "*** custom projects that require integration with the Google Analytics Measurement API -- inquiries can   ***",
        "*** be directed to:                                                                                       ***",
        "***                                                                                                       ***",
        "*** [M] sales@coherentlogic.com                                                                           ***",
        "*** [T] +1.571.306.3403 (GMT-5)                                                                           ***",
        "***                                                                                                       ***",
        "*************************************************************************************************************"
    };

    static {

        WelcomeMessage welcomeMessage = new WelcomeMessage();

        for (String next : WELCOME_MESSAGE) {
            welcomeMessage.addText(next);
        }

        welcomeMessage.display();
    }

    public static final String GOOGLE_ANALYTICS_TRACKING_KEY = "GOOGLE_ANALYTICS_TRACKING",
        GOOGLE_ANALYTICS_URL = "http://www.google-analytics.com/collect";

    public QueryBuilder(RestTemplate restTemplate) {
        this(restTemplate, GOOGLE_ANALYTICS_URL);
    }

    public QueryBuilder(RestTemplate restTemplate, String uri) {
        super(restTemplate, uri);
    }

    public QueryBuilder(RestTemplate restTemplate, UriBuilder uriBuilder) {
        super(restTemplate, uriBuilder);
    }

    public QueryBuilder withV (String protocolVersion) {

        addParameter("v", protocolVersion);

        return this;
    }

    public QueryBuilder withV1 () {
        return withV("1");
    }

    public QueryBuilder withTid (String trackingId) {

        addParameter("tid", trackingId);
    
        return this;
    }

    String asBoolean (boolean value) {
        return value ? "1" : "0";
    }

    public QueryBuilder withAip (boolean anonymizeIP) {

        String value = asBoolean(anonymizeIP);

        addParameter("aip", value);
    
        return this;
    }

    public QueryBuilder withDs (String dataSource) {

        addParameter("ds", dataSource);

        return this;
    }

    public QueryBuilder withQt (long queueTimeMillis) {

        if (0 < queueTimeMillis)
            throw new InvalidQueueTime (queueTimeMillis);

        addParameter("qt", Long.toString(queueTimeMillis));

        return this;
    }

    /**
     * Cache Buster
     *
     * Optional.
     *
     * Used to send a random number in GET requests to ensure browsers and proxies don't cache hits. It should be sent
     * as the final parameter of the request since we've seen some 3rd party internet filtering software add additional
     * parameters to HTTP requests incorrectly. This value is not used in reporting.
     */
    public QueryBuilder withZ (String cacheBuster) {

        addParameter("z", cacheBuster);

        return this;
    }

    /**
     * Client ID
     *
     * Required for all hit types.
     *
     * This anonymously identifies a particular user, device, or browser instance. For the web, this is generally stored
     * as a first-party cookie with a two-year expiration. For mobile apps, this is randomly generated for each
     * particular instance of an application install. The value of this field should be a random UUID (version 4) as
     * described in http://www.ietf.org/rfc/rfc4122.txt
     */
    public QueryBuilder withCID (String clientId) {

        addParameter("cid", clientId);

        return this;
    }

    /**
     * Client ID
     *
     * Required for all hit types.
     *
     * This anonymously identifies a particular user, device, or browser instance. For the web, this is generally stored
     * as a first-party cookie with a two-year expiration. For mobile apps, this is randomly generated for each
     * particular instance of an application install. The value of this field should be a random UUID (version 4) as
     * described in http://www.ietf.org/rfc/rfc4122.txt
     */
    public QueryBuilder withCIDAsRandomUUID () {

        addParameter("cid", UUID.randomUUID().toString());

        return this;
    }

    public QueryBuilder withUID (String userId) {

        addParameter("uid", userId);

        return this;
    }

    public QueryBuilder withSc (String sessionControl) {

        addParameter("sc", sessionControl);

        return this;
    }

    public QueryBuilder withUip (String ipOverride) {

        addParameter("uip", ipOverride);

        return this;
    }

    public QueryBuilder withUa (String userAgentOverride) {

        addParameter("ua", userAgentOverride);

        return this;
    }

    public QueryBuilder withT (String hitType) {

        addParameter("t", hitType);

        return this;
    }

    public static final String PAGE_VIEW = "pageview", 
        SCREEN_VIEW = "screenview",
        EVENT= "event",
        TRANSACTION= "transaction",
        ITEM= "item",
        SOCIAL= "social",
        EXCEPTION= "exception",
        TIMING= "timing";

    public QueryBuilder withTAsPageView () {
        return withT (PAGE_VIEW);
    }

    public QueryBuilder withTAsScreenView () {
        return withT (SCREEN_VIEW);
    }

    public QueryBuilder withTAsEvent () {
        return withT (EVENT);
    }

    public QueryBuilder withTAsTransaction () {
        return withT (TRANSACTION);
    }

    public QueryBuilder withTAsItem () {
        return withT (ITEM);
    }

    public QueryBuilder withTAsSocial () {
        return withT (SOCIAL);
    }

    public QueryBuilder withTAsException () {
        return withT (EXCEPTION);
    }

    public QueryBuilder withTAsTiming () {
        return withT (TIMING);
    }

    static void checkSizeOf (String parameterName, String parameterValue, int maxLengthInBytes) {

        Utils.assertNotNull(parameterName, parameterValue);

        if (maxLengthInBytes < parameterValue.getBytes().length) {
            throw new MaxLengthInBytesExceededException (parameterName, parameterValue, maxLengthInBytes);
        }
    }

    public QueryBuilder withEc (String eventCategory) {

        checkSizeOf ("eventCategory", eventCategory, 150);

        addParameter("ec", eventCategory);

        return this;
    }

    public QueryBuilder withAn (String applicationName) {

        checkSizeOf ("applicationName", applicationName, 100);

        addParameter("an", applicationName);

        return this;
    }

    public QueryBuilder withEa (String eventAction) {

        checkSizeOf ("eventAction", eventAction, 500);

        addParameter("ea", eventAction);

        return this;
    }

    public QueryBuilder withAv (String applicationVersion) {

        checkSizeOf ("applicationVersion", applicationVersion, 100);

        addParameter("av", applicationVersion);

        return this;
    }

    public QueryBuilder withEl (String eventLabel) {

        checkSizeOf ("eventLabel", eventLabel, 500);

        addParameter("el", eventLabel);

        return this;
    }

    @Override
    protected String getKey() {
        return "The key is not used so everything will be pass-through.";
    }

    @Override
    protected <T> T doExecute(Class<T> type) {

        UriBuilder uriBuilder = getUriBuilder();

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        URI uri = uriBuilder.build();

        HttpEntity<T> response = getRestTemplate ().exchange(
            uri,
            HttpMethod.POST,
            entity,
            type
        );

        return response.getBody();
    }

    public String doPost () {
        return doGet(String.class);
    }
}
