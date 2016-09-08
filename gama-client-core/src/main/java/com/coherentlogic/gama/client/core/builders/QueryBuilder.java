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

    /**
     * Protocol Version
     *
     * Required for all hit types.
     *
     * The Protocol version. The current value is '1'. This will only change when there are changes made that are not
     * backwards compatible.
     *
     * Example value: 1
     */
    public QueryBuilder withV (String protocolVersion) {

        addParameter("v", protocolVersion);

        return this;
    }

    /**
     * Protocol Version
     *
     * Required for all hit types.
     *
     * The Protocol version. The current value is '1'. This will only change when there are changes made that are not
     * backwards compatible.
     */
    public QueryBuilder withV1 () {
        return withV("1");
    }

    /**
     * Tracking ID / Web Property ID
     *
     * Required for all hit types.
     *
     * The tracking ID / web property ID. The format is UA-XXXX-Y. All collected data is associated by this ID.
     *
     * Example value: UA-XXXX-Y
     */
    public QueryBuilder withTid (String trackingId) {

        addParameter("tid", trackingId);
    
        return this;
    }

    /**
     * Converts the value to either 0 or 1.
     */
    String asBoolean (boolean value) {
        return value ? "1" : "0";
    }

    /**
     * Anonymize IP
     * 
     * Optional.
     *
     * When present, the IP address of the sender will be anonymized. For example, the IP will be anonymized if any of
     * the following parameters are present in the payload: &aip=, &aip=0, or &aip=1
     *
     * Example value: 1
     *
     * @todo Consider adding a method that takes a String "0" or "1".
     */
    public QueryBuilder withAip (boolean anonymizeIP) {

        String value = asBoolean(anonymizeIP);

        addParameter("aip", value);
    
        return this;
    }

    /**
     * Data Source
     *
     * Optional.
     *
     * Indicates the data source of the hit. Hits sent from analytics.js will have data source set to 'web'; hits sent
     * from one of the mobile SDKs will have data source set to 'app'.
     *
     * Example value: web
     * Example value: app
     * Example value: call center
     * Example value: crm
     */
    public QueryBuilder withDs (String dataSource) {

        addParameter("ds", dataSource);

        return this;
    }

    /**
     * Queue Time
     *
     * Optional.
     *
     * Used to collect offline / latent hits. The value represents the time delta (in milliseconds) between when the hit
     * being reported occurred and the time the hit was sent. The value must be greater than or equal to 0. Values
     * greater than four hours may lead to hits not being processed.
     *
     * Example value: 560
     */
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
     *
     * Example value: 289372387623
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
     *
     * Example value: 35009a79-1a05-49d7-b876-2b884d0f825b
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

    /**
     * User ID
     *
     * Optional.
     *
     * This is intended to be a known identifier for a user provided by the site owner/tracking library user. It must
     * not itself be PII (personally identifiable information). The value should never be persisted in GA cookies or
     * other Analytics provided storage.
     *
     * Example value: as8eknlll
     */
    public QueryBuilder withUID (String userId) {

        addParameter("uid", userId);

        return this;
    }

    /**
     * Session Control
     *
     * Optional.
     *
     * Used to control the session duration. A value of 'start' forces a new session to start with this hit and 'end'
     * forces the current session to end with this hit. All other values are ignored.
     *
     * Example value: start
     * Example value: end
     */
    public QueryBuilder withSc (String sessionControl) {

        addParameter("sc", sessionControl);

        return this;
    }

    /**
     * IP Override
     *
     * Optional.
     *
     * The IP address of the user. This should be a valid IP address in IPv4 or IPv6 format. It will always be
     * anonymized just as though &aip (anonymize IP) had been used.
     *
     * Example value: 1.2.3.4
     */
    public QueryBuilder withUip (String ipOverride) {

        addParameter("uip", ipOverride);

        return this;
    }

    /**
     * User Agent Override
     *
     * Optional.
     *
     * The User Agent of the browser. Note that Google has libraries to identify real user agents. Hand crafting your
     * own agent could break at any time.
     *
     * Example value: Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14
     */
    public QueryBuilder withUa (String userAgentOverride) {

        addParameter("ua", userAgentOverride);

        return this;
    }

    /**
     * Geographical Override
     *
     * Optional.
     *
     * The geographical location of the user. The geographical ID should be a two letter country code or a criteria ID
     * representing a city or region (see
     * http://developers.google.com/analytics/devguides/collection/protocol/v1/geoid). This parameter takes precedent
     * over any location derived from IP address, including the IP Override parameter. An invalid code will result in
     * geographical dimensions to be set to '(not set)'.
     *
     * Example value: US
     * Example value: 21137
     */
    public QueryBuilder withGeoID (String geographicalOverride) {

        addParameter("geoid", geographicalOverride);

        return this;
    }

    /**
     * Document Referrer
     *
     * Optional.
     *
     * Specifies which referral source brought traffic to a website. This value is also used to compute the traffic
     * source. The format of this value is a URL.
     *
     * Example value: http://example.com
     */
    public QueryBuilder withDr (String documentReferrer) {

        checkSizeOf("documentReferrer", documentReferrer, 2048);

        addParameter("dr", documentReferrer);

        return this;
    }

    /**
     * Campaign Name
     *
     * Optional.
     *
     * Specifies the campaign name.
     *
     * Example value: (direct)
     */
    public QueryBuilder withCn (String campaignName) {

        checkSizeOf("campaignName", campaignName, 100);

        addParameter("cn", campaignName);

        return this;
    }

    /**
     * Campaign Source
     *
     * Optional.
     *
     * Specifies the campaign source.
     *
     * Example value: (direct)
     */
    public QueryBuilder withCs (String campaignSource) {

        checkSizeOf("campaignSource", campaignSource, 100);

        addParameter("cs", campaignSource);

        return this;
    }

    /**
     * Campaign Medium
     *
     * Optional.
     *
     * Specifies the campaign medium.
     *
     * Example value: organic
     */
    public QueryBuilder withCm (String campaignMedium) {

        checkSizeOf("campaignMedium", campaignMedium, 50);

        addParameter("cm", campaignMedium);

        return this;
    }

    /**
     * Campaign Keyword
     *
     * Optional.
     *
     * Specifies the campaign keyword.
     *
     * Example value: Blue Shoes
     */
    public QueryBuilder withCk (String campaignKeyword) {

        checkSizeOf("campaignKeyword", campaignKeyword, 500);

        addParameter("ck", campaignKeyword);

        return this;
    }

    /**
     * Campaign Content
     *
     * Optional.
     *
     * Specifies the campaign content.
     *
     * Example value: content
     */
    public QueryBuilder withCc (String campaignContent) {

        checkSizeOf("campaignContent", campaignContent, 500);

        addParameter("cc", campaignContent);

        return this;
    }

    /**
     * Campaign ID
     *
     * Optional.
     *
     * Specifies the campaign ID.
     *
     * Example value: ID
     */
    public QueryBuilder withCi (String campaignID) {

        checkSizeOf("campaignID", campaignID, 100);

        addParameter("ci", campaignID);

        return this;
    }

    /**
     * Google AdWords ID
     *
     * Optional.
     *
     * Specifies the Google AdWords Id.
     *
     * Example value: CL6Q-OXyqKUCFcgK2goddQuoHg
     */
    public QueryBuilder withGclid (String googleAdWordsID) {

        addParameter("gclid", googleAdWordsID);

        return this;
    }

    /**
     * Google Display Ads ID
     *
     * Optional.
     *
     * Specifies the Google Display Ads Id.
     *
     * Example value: d_click_id
     */
    public QueryBuilder withDclid (String googleDisplayAdsID) {

        addParameter("gclid", googleDisplayAdsID);

        return this;
    }

    /**
     * Screen Resolution
     *
     * Optional.
     *
     * Specifies the screen resolution.
     *
     * Example value: 800x600
     */
    public QueryBuilder withSr (String screenResolution) {

    	checkSizeOf("screenResolution", screenResolution, 20);

        addParameter("sr", screenResolution);

        return this;
    }

    /**
     * Viewport size
     *
     * Optional.
     *
     * Specifies the viewable area of the browser / device.
     *
     * Example value: 123x456
     */
    public QueryBuilder withVp (String viewportSize) {

    	checkSizeOf("viewportSize", viewportSize, 20);

        addParameter("vp", viewportSize);

        return this;
    }

    /**
     * Document Encoding
     *
     * Optional.
     *
     * Specifies the character set used to encode the page / document.
     *
     * Example value: UTF-8
     */
    public QueryBuilder withDe (String documentEncoding) {

    	checkSizeOf("documentEncoding", documentEncoding, 20);

        addParameter("de", documentEncoding);

        return this;
    }

    /**
     * Screen Colors
     *
     * Optional.
     *
     * Specifies the screen color depth.
     *
     * Example value: 24-bits
     */
    public QueryBuilder withSd (String screenColors) {

    	checkSizeOf("screenColors", screenColors, 20);

        addParameter("sd", screenColors);

        return this;
    }

    /**
     * User Language
     *
     * Optional.
     *
     * Specifies the language.
     *
     * Example value: en-us
     */
    public QueryBuilder withUl (String userLanguage) {

    	checkSizeOf("userLanguage", userLanguage, 20);

        addParameter("ul", userLanguage);

        return this;
    }

    /**
     * Java Enabled
     *
     * Optional.
     *
     * Specifies whether Java was enabled.
     *
     * Example value: 1
     */
    public QueryBuilder withJe (boolean javaEnabled) {

        addParameter("je", asBoolean(javaEnabled));

        return this;
    }

    /**
     * Flash Version
     *
     * Optional.
     *
     * Specifies the flash version.
     *
     * Example value: 10 1 r103
     */
    public QueryBuilder withFl (String flashVersion) {

    	checkSizeOf("flashVersion", flashVersion, 20);

        addParameter("fl", flashVersion);

        return this;
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: pageview
     */
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

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: pageview
     */
    public QueryBuilder withTAsPageView () {
        return withT (PAGE_VIEW);
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: screenview
     */
    public QueryBuilder withTAsScreenView () {
        return withT (SCREEN_VIEW);
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: event
     */
    public QueryBuilder withTAsEvent () {
        return withT (EVENT);
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: transaction
     */
    public QueryBuilder withTAsTransaction () {
        return withT (TRANSACTION);
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: item
     */
    public QueryBuilder withTAsItem () {
        return withT (ITEM);
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: social
     */
    public QueryBuilder withTAsSocial () {
        return withT (SOCIAL);
    }

    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: exception
     */
    public QueryBuilder withTAsException () {
        return withT (EXCEPTION);
    }

    /**
     * Non-Interaction Hit
     *
     * Optional.
     *
     * Specifies that a hit be considered non-interactive.
     *
     * Example value: 1
     */
    public QueryBuilder withNi (boolean nonInteractionHit) {

        addParameter("ni", asBoolean(nonInteractionHit));

        return this;
    }

    /**
     * Document location URL
     *
     * Optional.
     *
     * Use this parameter to send the full URL (document location) of the page on which content resides. You can use the
     * &dh and &dp parameters to override the hostname and path + query portions of the document location, accordingly.
     * The JavaScript clients determine this parameter using the concatenation of the
     * document.location.origin + document.location.pathname + document.location.search browser parameters. Be sure to
     * remove any user authentication or other private information from the URL if present. For 'pageview' hits, either
     * &dl or both &dh and &dp have to be specified for the hit to be valid.
     *
     * Example value: http://foo.com/home?a=b
     */
    public QueryBuilder withDl (String documentLocationURL) {

    	checkSizeOf("documentLocationURL", documentLocationURL, 2048);

        addParameter("dl", documentLocationURL);

        return this;
    }
    
    /**
     * Document Host Name
     *
     * Optional.
     *
     * Specifies the hostname from which content was hosted.
     *
     * Example value: foo.com
     */
    public QueryBuilder withDh (String documentHostName) {

    	checkSizeOf("documentHostName", documentHostName, 100);

        addParameter("dh", documentHostName);

        return this;
    }

    /**
     * Document Path
     *
     * Optional.
     *
     * The path portion of the page URL. Should begin with '/'. For 'pageview' hits, either &dl or both &dh and &dp have
     * to be specified for the hit to be valid.
     *
     * Example value: /foo
     */
    public QueryBuilder withDp (String documentPath) {

    	checkSizeOf("documentPath", documentPath, 2048);

        addParameter("dp", documentPath);

        return this;
    }

    /**
     * Document Title
     *
     * Optional.
     *
     * The title of the page / document.
     */
    public QueryBuilder withDt (String documentTitle) {

    	checkSizeOf("documentTitle", documentTitle, 1500);

        addParameter("dt", documentTitle);

        return this;
    }


    
    /**
     * Hit type
     *
     * Required for all hit types.
     *
     * The type of hit. Must be one of 'pageview', 'screenview', 'event', 'transaction', 'item', 'social', 'exception',
     * 'timing'.
     *
     * Example value: timing
     */
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
