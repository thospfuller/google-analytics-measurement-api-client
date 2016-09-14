package com.coherentlogic.gama.client.core.builders;

import java.math.BigDecimal;
import java.net.URI;
import java.text.MessageFormat;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.coherentlogic.coherent.data.model.core.builders.rest.AbstractRESTQueryBuilder;
import com.coherentlogic.coherent.data.model.core.util.Utils;
import com.coherentlogic.coherent.data.model.core.util.WelcomeMessage;
import com.coherentlogic.gama.client.core.exceptions.InvalidQueueTime;
import com.coherentlogic.gama.client.core.exceptions.MaxLengthInBytesExceededException;
import com.coherentlogic.gama.client.core.exceptions.NegativeValueException;
import com.coherentlogic.gama.client.core.exceptions.PostFailedException;
import com.coherentlogic.gama.client.core.exceptions.ValueOutOfBoundsException;

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

    public QueryBuilder() {
        this(new RestTemplate (), GOOGLE_ANALYTICS_URL);
    }

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
     * @todo Move this to the Utils class.
     * @todo Use more generalized exception.
     */
    QueryBuilder assertNotNegative (String name, long value) {

        Utils.assertNotNull(name, value);

        if (value < 0)
            throw new NegativeValueException (name, value);

        return this;
    }

    /**
     * @todo Move this to the Utils class.
     * @todo Use more generalized exception.
     */
    QueryBuilder assertNotNegative (String name, int value) {

        Utils.assertNotNull(name, value);

        if (value < 0)
            throw new NegativeValueException (name, value);

        return this;
    }

    QueryBuilder checkSizeOf (String parameterName, String parameterValue, int maxLengthInBytes) {

        Utils.assertNotNull(parameterName, parameterValue);

        if (maxLengthInBytes < parameterValue.getBytes().length) {
            throw new MaxLengthInBytesExceededException (parameterName, parameterValue, maxLengthInBytes);
        }

        return this;
    }

    /**
     * Converts the value to either 0 or 1.
     */
    String asBoolean (boolean value) {
        return value ? "1" : "0";
    }

    QueryBuilder assertBetween (String name, int begin, int end, int actual) {

        if (! (begin <= actual && actual <= end))
            throw new ValueOutOfBoundsException(name, begin, end, actual);

        return this;
    }

    public static final String V = "v", ONE = "1";

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

        addParameter(V, protocolVersion);

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
        return withV(ONE);
    }

    public static final String TID = "tid";

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

        addParameter(TID, trackingId);
    
        return this;
    }

    public static final String AIP = "aip";

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

        addParameter(AIP, value);
    
        return this;
    }

    public static final String DS = "ds";

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

        addParameter(DS, dataSource);

        return this;
    }

    public static final String QT = "qt";

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

        if (queueTimeMillis <= 0)
            throw new InvalidQueueTime (queueTimeMillis);

        addParameter(QT, Long.toString(queueTimeMillis));

        return this;
    }

    public static final String Z = "z";

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

        addParameter(Z, cacheBuster);

        return this;
    }

    public static final String CID = "cid";

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

        addParameter(CID, clientId);

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
        return withCID(UUID.randomUUID().toString());
    }

    public static final String UID = "uid";

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

        addParameter(UID, userId);

        return this;
    }

    public static final String SC = "sc";

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

        addParameter(SC, sessionControl);

        return this;
    }

    public static final String UIP = "uip";

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

        addParameter(UIP, ipOverride);

        return this;
    }

    public static final String UA = "ua";

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

        addParameter(UA, userAgentOverride);

        return this;
    }

    public static final String GEOID = "geoid";

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

        addParameter(GEOID, geographicalOverride);

        return this;
    }

    public static final String DR = "dr";

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

        addParameter(DR, documentReferrer);

        return this;
    }

    public static final String CN = "cn";

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

        addParameter(CN, campaignName);

        return this;
    }

    public static final String CS = "cs";

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

        addParameter(CS, campaignSource);

        return this;
    }

    public static final String CM = "cm";

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

        addParameter(CM, campaignMedium);

        return this;
    }

    public static final String CK = "ck";

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

        addParameter(CK, campaignKeyword);

        return this;
    }

    public static final String CC = "cc";

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

        addParameter(CC, campaignContent);

        return this;
    }

    public static final String CI = "ci";

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

        addParameter(CI, campaignID);

        return this;
    }

    public static final String GCLID = "gclid";

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

        addParameter(GCLID, googleAdWordsID);

        return this;
    }

    public static final String DCLID = "dclid";

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

        addParameter(DCLID, googleDisplayAdsID);

        return this;
    }

    public static final String SR = "sr";

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

        addParameter(SR, screenResolution);

        return this;
    }

    public static final String VP = "vp";

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

        addParameter(VP, viewportSize);

        return this;
    }

    public static final String DE = "de";

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

        addParameter(DE, documentEncoding);

        return this;
    }

    public static final String SD = "sd";

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

        addParameter(SD, screenColors);

        return this;
    }

    public static final String UL = "ul";

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

        addParameter(UL, userLanguage);

        return this;
    }

    public static final String JE = "je";

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

        addParameter(JE, asBoolean(javaEnabled));

        return this;
    }

    public static final String FL = "fl";

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

        addParameter(FL, flashVersion);

        return this;
    }

    public static final String T = "t";

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

        addParameter(T, hitType);

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

    public static final String NI = "ni";

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

        addParameter(NI, asBoolean(nonInteractionHit));

        return this;
    }

    public static final String DL = "dl";

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

        addParameter(DL, documentLocationURL);

        return this;
    }

    public static final String DH = "dh";

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

        addParameter(DH, documentHostName);

        return this;
    }

    public static final String DP = "dp";

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

        addParameter(DP, documentPath);

        return this;
    }

    public static final String DT = "dt";

    /**
     * Document Title
     *
     * Optional.
     *
     * The title of the page / document.
     *
     * Example value: Settings
     */
    public QueryBuilder withDt (String documentTitle) {

        checkSizeOf("documentTitle", documentTitle, 1500);

        addParameter(DT, documentTitle);

        return this;
    }

    public static final String CD = "cd";

    /**
     * Screen Name
     *
     * Required for screenview hit type.
     *
     * This parameter is optional on web properties, and required on mobile properties for screenview hits, where it is
     * used for the 'Screen Name' of the screenview hit. On web properties this will default to the unique URL of the
     * page by either using the &dl parameter as-is or assembling it from &dh and &dp.
     *
     * Example value: High Scores
     */
    public QueryBuilder withCd (String screenName) {

        checkSizeOf("screenName", screenName, 2048);

        addParameter(CD, screenName);

        return this;
    }

    public static final String LINKID = "linkid";

    /**
     * Link ID
     *
     * Optional.
     *
     * The ID of a clicked DOM element, used to disambiguate multiple links to the same URL in In-Page Analytics reports
     * when Enhanced Link Attribution is enabled for the property.
     *
     * Example value: nav_bar
     */
    public QueryBuilder withLinkid (String linkID) {

        addParameter(LINKID, linkID);

        return this;
    }

    public static final String AN = "an";

    /**
     * Application Name
     *
     * Optional.
     *
     * Specifies the application name. This field is required for any hit that has app related data (i.e., app version,
     * app ID, or app installer ID). For hits sent to web properties, this field is optional.
     *
     * Example value: My App
     */
    public QueryBuilder withAn (String applicationName) {

        checkSizeOf("applicationName", applicationName, 100);

        addParameter(AN, applicationName);

        return this;
    }

    public static final String AID = "aid";

    /**
     * Application ID
     *
     * Optional.
     *
     * Application identifier.
     *
     * Example value: com.company.app
     */
    public QueryBuilder withAid (String applicationID) {

        checkSizeOf("applicationID", applicationID, 150);

        addParameter(AID, applicationID);

        return this;
    }

    public static final String AV = "av";

    /**
     * Application Version
     *
     * Optional.
     *
     * Specifies the application version.
     *
     * Example value: 1.2
     */
    public QueryBuilder withAv (String applicationVersion) {

        checkSizeOf ("applicationVersion", applicationVersion, 100);

        addParameter(AV, applicationVersion);

        return this;
    }

    public static final String AIID = "aiid";

    /**
     * Application Installer ID
     *
     * Optional.
     *
     * Application installer identifier.
     *
     * Example value: com.platform.vending
     */
    public QueryBuilder withAiid (String applicationInstallerID) {

        checkSizeOf ("applicationInstallerID", applicationInstallerID, 150);

        addParameter(AIID, applicationInstallerID);

        return this;
    }

    public static final String EC = "ec";

    /**
     * Event Category
     *
     * Required for event hit type.
     *
     * Specifies the event category. Must not be empty.
     *
     * Example value: Category
     */
    public QueryBuilder withEc (String eventCategory) {

        checkSizeOf ("eventCategory", eventCategory, 150);

        addParameter(EC, eventCategory);

        return this;
    }

    public static final String EA = "ea";

    /**
     * Event Action
     *
     * Required for event hit type.
     *
     * Specifies the event action. Must not be empty.
     *
     * Example value: Action
     */
    public QueryBuilder withEa (String eventAction) {

        checkSizeOf ("eventAction", eventAction, 500);

        addParameter(EA, eventAction);

        return this;
    }

    public static final String EL = "el";

    /**
     * Event Label
     *
     * Optional.
     *
     * Specifies the event label.
     *
     * Example value: Label
     */
    public QueryBuilder withEl (String eventLabel) {

        checkSizeOf ("eventLabel", eventLabel, 500);

        addParameter(EL, eventLabel);

        return this;
    }

    public static final String EV = "ev";

    /**
     * Event Value
     *
     * Optional.
     *
     * Specifies the event value. Values must be non-negative.
     *
     * Example value: 55
     *
     * @todo Unit test negative values.
     */
    public QueryBuilder withEv (int eventValue) {

        assertNotNegative("eventValue", eventValue);

        addParameter(EV, Integer.toString(eventValue));

        return this;
    }

    public static final String TI = "ti";

    /**
     * Below is from E-Commerce.
     *
     * Transaction ID
     *
     * Required for transaction hit type.
     * Required for item hit type.
     *
     * A unique identifier for the transaction. This value should be the same for both the Transaction hit and Items
     * hits associated to the particular transaction.
     *
     * Example value: OD564
     * 
     * -----
     * 
     * Below is from Enhanced E-Commerce, which does not have a size associated with the transactionID, however this
     * limitation should still apply.
     *
     * Transaction ID
     *
     * Optional.
     *
     * The transaction ID. This is an additional parameter that can be sent when Product Action is set to 'purchase' or
     * 'refund'. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: T1234
     * Example usage: ti=T1234
     */
    public QueryBuilder withTi (String transactionID) {

        checkSizeOf ("transactionID", transactionID, 500);

        addParameter(TI, transactionID);

        return this;
    }

    public static final String TA = "ta";

    /**
     * Transaction Affiliation
     *
     * Optional.
     *
     * Specifies the affiliation or store name.
     *
     * Example value: Member
     * 
     * -----
     *
     * Below appears in Enhanced E-Commerce.
     *
     * Affiliation
     *
     * Optional.
     *
     * The store or affiliation from which this transaction occurred. This is an additional parameter that can be sent
     * when Product Action is set to 'purchase' or 'refund'. For analytics.js the Enhanced Ecommerce plugin must be
     * installed before using this field.
     *
     * Example value: Google Store
     * Example usage: ta=Google%20Store
     */
    public QueryBuilder withTa (String transactionAffiliation) {

        checkSizeOf ("transactionAffiliation", transactionAffiliation, 500);

        addParameter(TA, transactionAffiliation);

        return this;
    }

    public static final String TR = "tr";

    /**
     * Transaction Revenue
     *
     * Optional.
     *
     * Specifies the total revenue associated with the transaction. This value should include any shipping or tax costs.
     *
     * Example value: 15.47
     */
    public QueryBuilder withTr (String transactionRevenue) {

        addParameter(TR, transactionRevenue);

        return this;
    }

    /**
     * Transaction Revenue
     *
     * Optional.
     *
     * Specifies the total revenue associated with the transaction. This value should include any shipping or tax costs.
     *
     * Example value: 15.47
     *
     * @todo Should we reject negative values?
     */
    public QueryBuilder withTr (BigDecimal transactionRevenue) {

        addParameter(TR, transactionRevenue);

        return this;
    }

    public static final String TT = "tt";

    /**
     * Transaction Tax
     *
     * Optional.
     *
     * Specifies the total tax of the transaction.
     *
     * Example value: 11.20
     *
     * @todo Should we reject negative values?
     */
    public QueryBuilder withTt (BigDecimal transactionTax) {

        addParameter(TT, transactionTax);

        return this;
    }

    public static final String IN = "in";

    /**
     * Item Name
     *
     * Required for item hit type.
     *
     * Specifies the item name.
     *
     * Example value: Shoe
     */
    public QueryBuilder withIn (String itemName) {

        checkSizeOf ("itemName", itemName, 500);

        addParameter(IN, itemName);

        return this;
    }

    public static final String IP = "ip";

    /**
     * Item Price
     *
     * Optional.
     *
     * Specifies the price for a single item / unit.
     *
     * Example value: 3.50
     *
     * @todo Should we reject negative values?
     */
    public QueryBuilder withIp (String itemPrice) {

        addParameter(IP, itemPrice);

        return this;
    }

    /**
     * Item Price
     *
     * Optional.
     *
     * Specifies the price for a single item / unit.
     *
     * Example value: 3.50
     *
     * @todo Should we reject negative values?
     */
    public QueryBuilder withIp (BigDecimal itemPrice) {

        addParameter(IP, itemPrice);

        return this;
    }

    public static final String IQ = "iq";

    /**
     * Event Value
     *
     * Optional.
     *
     * Specifies the event value. Values must be non-negative.
     *
     * Example value: 55
     *
     * @todo Unit test negative values.
     */
    public QueryBuilder withIq (String itemQuantity) {

        addParameter(IQ, itemQuantity);

        return this;
    }

    /**
     * Event Value
     *
     * Optional.
     *
     * Specifies the event value. Values must be non-negative.
     *
     * Example value: 55
     *
     * @todo Unit test negative values.
     * @todo Change int to Integer and change the assertNotNegative so it takes a Number.
     */
    public QueryBuilder withIq (int itemQuantity) {

        assertNotNegative("itemQuantity", itemQuantity);

        addParameter("iq", Integer.toString(itemQuantity));

        return this;
    }

    public static final String IC = "ic";

    /**
     * Item Code
     *
     * Optional.
     *
     * Specifies the SKU or item code.
     *
     * Example value: SKU47
     */
    public QueryBuilder withIc (String itemCode) {

        checkSizeOf ("itemCode", itemCode, 500);

        addParameter(IC, itemCode);

        return this;
    }

    public static final String IV = "iv";

    /**
     * Item Category
     *
     * Optional.
     *
     * Specifies the category that the item belongs to.
     *
     * Example value: Blue
     */
    public QueryBuilder withIv (String itemCategory) {

        checkSizeOf ("itemCategory", itemCategory, 500);

        addParameter(IV, itemCategory);

        return this;
    }

    public static final String CU = "cu";

    /**
     * Currency Code
     *
     * Optional.
     *
     * When present indicates the local currency for all transaction currency values. Value should be a valid ISO 4217
     * currency code.
     *
     * Example value: EUR
     */
    public QueryBuilder withCu (String currencyCode) {

        checkSizeOf ("currencyCode", currencyCode, 500);

        addParameter(CU, currencyCode);

        return this;
    }

    public static final String PR_N_ID = "pr{0}id";

    /**
     * Product SKU
     *
     * Optional.
     *
     * The SKU of the product. Product index must be a positive integer between 1 and 200, inclusive. For analytics.js
     * the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: P12345
     * Example usage: pr1id=P12345
     */
    public QueryBuilder withPrNId (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        checkSizeOf ("value", value, 500);

        addParameter(MessageFormat.format(PR_N_ID, productIndexN), value);

        return this;
    }

    public static final String PR_N_NM = "pr{0}nm";

    /**
     * Product Name
     *
     * Optional.
     *
     * The name of the product. Product index must be a positive integer between 1 and 200, inclusive. For analytics.js
     * the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: Android T-Shirt
     */
    public QueryBuilder withPrNNm (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        checkSizeOf ("value", value, 500);

        addParameter(MessageFormat.format(PR_N_NM, productIndexN), value);

        return this;
    }

    public static final String PR_N_BR = "pr{0}br";

    /**
     * Product Brand
     *
     * Optional.
     *
     * The brand associated with the product. Product index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: Google
     */
    public QueryBuilder withPrNBr (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        checkSizeOf ("value", value, 500);

        addParameter(MessageFormat.format(PR_N_BR, productIndexN), value);

        return this;
    }

    public static final String PR_N_CA = "pr{0}ca";

    /**
     * Product Category
     *
     * Optional.
     *
     * The category to which the product belongs. Product index must be a positive integer between 1 and 200, inclusive. The product category parameter can be hierarchical. Use / as a delimiter to specify up to 5-levels of hierarchy. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field. 
     *
     * Example value: Apparel
     * Example usage: pr1ca=Apparel
     *
     * Example value: Apparel/Mens/T-Shirts
     * Example usage: pr1ca=Apparel%2FMens%2FT-Shirts
     */
    public QueryBuilder withPrNCa (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        checkSizeOf ("value", value, 500);

        addParameter(MessageFormat.format(PR_N_CA, productIndexN), value);

        return this;
    }

    public static final String PR_N_VA = "pr{0}va";

    /**
     * Product Variant
     *
     * Optional.
     *
     * The variant of the product. Product index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: Black
     * Example usage: pr1va=Black
     */
    public QueryBuilder withPrNVa (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        checkSizeOf ("value", value, 500);

        addParameter(MessageFormat.format(PR_N_VA, productIndexN), value);

        return this;
    }

    public static final String PR_N_PR = "pr{0}pr";

    /**
     * Product Price
     *
     * Optional.
     *
     * The unit price of a product. Product index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 29.20
     * Example usage: pr1pr=29.20
     */
    public QueryBuilder withPrNPr (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);

        addParameter(MessageFormat.format(PR_N_PR, productIndexN), value);

        return this;
    }

    /**
     * Product Price
     *
     * Optional.
     *
     * The unit price of a product. Product index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 29.20
     * Example usage: pr1pr=29.20
     */
    public QueryBuilder withPrNPr (int productIndexN, BigDecimal value) {

        assertBetween("productIndexN", 1, 200, productIndexN);

        addParameter(MessageFormat.format(PR_N_PR, productIndexN), value);

        return this;
    }

    public static final String PR_N_QT = "pr{0}qt";

    /**
     * Product Quantity
     *
     * Optional.
     *
     * The quantity of a product. Product index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 2
     * Example usage: pr1qt=2
     */
    public QueryBuilder withPrNQt (int productIndexN, Integer value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        assertNotNegative("value", value);

        addParameter(MessageFormat.format(PR_N_QT, productIndexN), value);

        return this;
    }

    /**
     * Product Quantity
     *
     * Optional.
     *
     * The quantity of a product. Product index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 2
     * Example usage: pr1qt=2
     */
    public QueryBuilder withPrNQt (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);

        addParameter(MessageFormat.format(PR_N_QT, productIndexN), value);

        return this;
    }

    public static final String PR_N_CC = "pr{0}cc";

    /**
     * Product Coupon Code
     *
     * Optional.
     *
     * The coupon code associated with a product. Product index must be a positive integer between 1 and 200, inclusive.
     * For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     * 
     * Example value: SUMMER_SALE13
     * Example usage: pr1cc=SUMMER_SALE13
     */
    public QueryBuilder withPrNCc (int productIndexN, Integer value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        assertNotNegative("value", value);

        addParameter(MessageFormat.format(PR_N_CC, productIndexN), value);

        return this;
    }

    /**
     * Product Coupon Code
     *
     * Optional.
     *
     * The coupon code associated with a product. Product index must be a positive integer between 1 and 200, inclusive.
     * For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     * 
     * Example value: SUMMER_SALE13
     * Example usage: pr1cc=SUMMER_SALE13
     */
    public QueryBuilder withPrNCc (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        checkSizeOf("value", value, 500);

        addParameter(MessageFormat.format(PR_N_CC, productIndexN), value);

        return this;
    }

    public static final String PR_N_PS = "pr{0}ps";

    /**
     * Product Position
     *
     * Optional.
     *
     * The product's position in a list or collection. Product index must be a positive integer between 1 and 200,
     * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 2
     * Example usage: pr1ps=2
     */
    public QueryBuilder withPrNPs (int productIndexN, String value) {

        assertBetween("productIndexN", 1, 200, productIndexN);

        addParameter(MessageFormat.format(PR_N_PS, productIndexN), value);

        return this;
    }

    /**
     * Product Position
     *
     * Optional.
     *
     * The product's position in a list or collection. Product index must be a positive integer between 1 and 200,
     * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 2
     * Example usage: pr1ps=2
     */
    public QueryBuilder withPrNPs (int productIndexN, Integer value) {

        assertBetween("productIndexN", 1, 200, productIndexN);
        assertNotNegative("value", value);

        addParameter(MessageFormat.format(PR_N_PS, productIndexN), value);

        return this;
    }

    public static final String PR_X_CD_Y = "pr{0}cd{1}";

    /**
     * Product Custom Dimension
     *
     * Optional.
     *
     * A product-level custom dimension where dimension index is a positive integer between 1 and 200, inclusive.
     * Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce
     * plugin must be installed before using this field.
     *
     * Example value: Member
     * Example usage: pr1cd2=Member
     *
     * @todo Consider changing N to X for consistency.
     */
    public QueryBuilder withPrXCdY (int productIndexX, int dimensionIndexY, String value) {

        assertBetween("productIndexX", 1, 200, productIndexX);
        assertBetween("dimensionIndexY", 1, 200, dimensionIndexY);

        addParameter(MessageFormat.format(PR_X_CD_Y, productIndexX, dimensionIndexY), value);

        return this;
    }

    public static final String PR_X_CM_Y = "pr{0}cm{1}";

    /**
     * Product Custom Metric
     *
     * Optional.
     *
     * A product-level custom metric where metric index is a positive integer between 1 and 200, inclusive. Product
     * index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin
     * must be installed before using this field.
     *
     * Example value: 28
     * Example usage: pr1cm2=28
     */
    public QueryBuilder withPrXCmY (int productIndexX, int metricIndexY, String value) {

        assertBetween("productIndexX", 1, 200, productIndexX);
        assertBetween("metricIndexY", 1, 200, metricIndexY);

        addParameter(MessageFormat.format(PR_X_CM_Y, productIndexX, metricIndexY), value);

        return this;
    }

    public static final String PA = "pa";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPa (String productAction) {

        addParameter(PA, productAction);

        return this;
    }

    public static final String DETAIL = "detail";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsDetail () {
        return withPa(DETAIL);
    }

    public static final String CLICK = "click";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsClick () {
        return withPa(CLICK);
    }

    public static final String ADD = "add";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsAdd () {
        return withPa(ADD);
    }

    public static final String REMOVE = "remove";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsRemove () {
        return withPa(REMOVE);
    }

    public static final String CHECKOUT = "checkout";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsCheckout () {
        return withPa(CHECKOUT);
    }

    public static final String CHECKOUT_OPTION = "checkout_option";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsCheckoutOption () {
        return withPa(CHECKOUT_OPTION);
    }

    public static final String PURCHASE = "purchase";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsPurchase () {
        return withPa(PURCHASE);
    }

    public static final String REFUND = "refund";

    /**
     * Product Action
     *
     * Optional.
     *
     * The role of the products included in a hit. If a product action is not specified, all product definitions
     * included with the hit will be ignored. Must be one of: detail, click, add, remove, checkout, checkout_option,
     * purchase, refund. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: detail
     * Example usage: pa=detail
     */
    public QueryBuilder withPaAsRefund () {
        return withPa(REFUND);
    }

    // Note that the GAM documentation has ti here too but this parameter is mentioned already earlier in the doc hence
    // we have implemented this already.

    // Note that the GAM documentation has ta here too but this parameter is mentioned already earlier in the doc hence
    // we have implemented this already.

    // Note that the GAM documentation has tr here too but this parameter is mentioned already earlier in the doc hence
    // we have implemented this already.

    // Note that the GAM documentation has tt here too but this parameter is mentioned already earlier in the doc hence
    // we have implemented this already.

    public static final String TS = "ts";

    /**
     * Shipping
     *
     * Optional.
     *
     * The shipping cost associated with the transaction. This is an additional parameter that can be sent when Product
     * Action is set to 'purchase' or 'refund'. For analytics.js the Enhanced Ecommerce plugin must be installed before
     * using this field.
     *
     * Example value: 3.55
     * Example usage: ts=3.55
     */
    public QueryBuilder withTs (String shipping) {

        addParameter(TS, shipping);

        return this;
    }

    /**
     * Shipping
     *
     * Optional.
     *
     * The shipping cost associated with the transaction. This is an additional parameter that can be sent when Product
     * Action is set to 'purchase' or 'refund'. For analytics.js the Enhanced Ecommerce plugin must be installed before
     * using this field.
     *
     * Example value: 3.55
     * Example usage: ts=3.55
     */
    public QueryBuilder withTs (BigDecimal shipping) {

        addParameter(TS, shipping);

        return this;
    }

    public static final String TCC = "tcc";

    /**
     * Coupon Code
     *
     * Optional.
     *
     * The transaction coupon redeemed with the transaction. This is an additional parameter that can be sent when
     * Product Action is set to 'purchase' or 'refund'. For analytics.js the Enhanced Ecommerce plugin must be installed
     * before using this field.
     *
     * Example value: SUMMER08
     * Example usage: tcc=SUMMER08
     */
    public QueryBuilder withTcc (String couponCode) {

        addParameter(TCC, couponCode);

        return this;
    }

    public static final String PAL = "pal";

    /**
     * Product Action List
     *
     * Optional.
     *
     * The list or collection from which a product action occurred. This is an additional parameter that can be sent
     * when Product Action is set to 'detail' or 'click'. For analytics.js the Enhanced Ecommerce plugin must be
     * installed before using this field.
     *
     * Example value: Search Results
     * Example usage: pal=Search%20Results
     */
    public QueryBuilder withPal (String productActionList) {

        addParameter(PAL, productActionList);

        return this;
    }

    public static final String COS = "cos";

    /**
     * Checkout Step
     *
     * Optional.
     *
     * The step number in a checkout funnel. This is an additional parameter that can be sent when Product Action is set
     * to 'checkout'. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 2
     * Example usage: cos=2
     */
    public QueryBuilder withCos (int checkoutStep) {

        addParameter(COS, checkoutStep);

        return this;
    }

    public static final String COL = "col";

    /**
     * Checkout Step Option
     *
     * Optional.
     *
     * Additional information about a checkout step. This is an additional parameter that can be sent when Product
     * Action is set to 'checkout'. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
     * field.
     *
     * Example value: Visa
     * Example usage: col=Visa
     */
    public QueryBuilder withCol (String checkoutStepOption) {

        addParameter(COL, checkoutStepOption);

        return this;
    }

    public static final String IL_X_NM = "il{0}nm";

    /**
     * Product Impression List Name
     *
     * Optional.
     *
     * The list or collection to which a product belongs. Impression List index must be a positive integer between 1 and
     * 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: Search Results
     * Example usage: il1nm=Search%20Results
     */
    public QueryBuilder withIlXNm (int listIndex, String productImpressionListName) {

        assertBetween("listIndex", 1, 200, listIndex);

        addParameter(
        	MessageFormat.format(IL_X_NM, listIndex),
        	productImpressionListName
        );

        return this;
    }

    public static final String IL_X_PI_Y_ID = "il{0}pi{1}id";

    /**
     * Product Impression SKU
     *
     * Optional.
     *
     * The product ID or SKU. Impression List index must be a positive integer between 1 and 200, inclusive. Product
     * index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin
     * must be installed before using this field.
     *
     * Example value: P67890
     * Example usage: il1pi2id=P67890
     */
    public QueryBuilder withIlXPiYId (int listIndex, int productIndex, String productImpressionSKU) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_ID, listIndex, productIndex), productImpressionSKU);

        return this;
    }

    public static final String IL_X_PI_Y_NM = "il{0}pi{1}nm";

    /**
     * Product Impression Name
     *
     * Optional.
     *
     * The name of the product. Impression List index must be a positive integer between 1 and 200, inclusive. Product
     * index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin
     * must be installed before using this field.
     *
     * Example value: Android T-Shirt
     * Example usage: il1pi2nm=Android%20T-Shirt
     */
    public QueryBuilder withIlXPiYNm (int listIndex, int productIndex, String productImpressionName) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_NM, listIndex, productIndex), productImpressionName);

        return this;
    }

    public static final String IL_X_PI_Y_BR = "il{0}pi{1}br";

    /**
     * Product Impression Brand
     *
     * Optional.
     *
     * The brand associated with the product. Impression List index must be a positive integer between 1 and 200,
     * inclusive. Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced
     * Ecommerce plugin must be installed before using this field.
     *
     * Example value: Google
     * Example usage: il1pi2br=Google
     */
    public QueryBuilder withIlXPiYBr (int listIndex, int productIndex, String productImpressionBrand) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_BR, listIndex, productIndex), productImpressionBrand);

        return this;
    }

    public static final String IL_X_PI_Y_CA = "il{0}pi{1}ca";

    /**
     * Product Impression Category
     *
     * Optional.
     *
     * The category to which the product belongs. Impression List index must be a positive integer between 1 and 200,
     * inclusive. Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced
     * Ecommerce plugin must be installed before using this field.
     *
     * Example value: Apparel
     * Example usage: il1pi2ca=Apparel
     */
    public QueryBuilder withIlXPiYCa (int listIndex, int productIndex, String productImpressionBrand) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_CA, listIndex, productIndex), productImpressionBrand);

        return this;
    }

    public static final String IL_X_PI_Y_VA = "il{0}pi{1}va";

    /**
     * Product Impression Variant
     *
     * Optional.
     *
     * The variant of the product. Impression List index must be a positive integer between 1 and 200, inclusive.
     * Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce
     * plugin must be installed before using this field.
     *
     * Example value: Black
     * Example usage: il1pi2va=Black
     */
    public QueryBuilder withIlXPiYVa (int listIndex, int productIndex, String productImpressionVariant) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_VA, listIndex, productIndex), productImpressionVariant);

        return this;
    }

    public static final String IL_X_PI_Y_PS = "il{0}pi{1}ps";

    /**
     * Product Impression Position
     *
     * Optional.
     *
     * The product's position in a list or collection. Impression List index must be a positive integer between 1 and
     * 200, inclusive. Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the
     * Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: 2
     * Example usage: il1pi2ps=2
     */
    public QueryBuilder withIlXPiYPs (int listIndex, int productIndex, String productImpressionPosition) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_PS, listIndex, productIndex), productImpressionPosition);

        return this;
    }

    public static final String IL_X_PI_Y_PR = "il{0}pi{1}pr";

    /**
     * Product Impression Price
     *
     * Optional.
     *
     * The price of a product. Impression List index must be a positive integer between 1 and 200, inclusive. Product
     * index must be a positive integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin
     * must be installed before using this field.
     *
     * Example value: 29.20
     * Example usage: il1pi2pr=29.20
     */
    public QueryBuilder withIlXPiYPr (int listIndex, int productIndex, String productImpressionPrice) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);

        addParameter(MessageFormat.format(IL_X_PI_Y_PR, listIndex, productIndex), productImpressionPrice);

        return this;
    }

    public static final String IL_X_PI_Y_CD_Z = "il{0}pi{1}cd{2}";

    /**
     * Product Impression Custom Dimension
     *
     * Optional.
     *
     * A product-level custom dimension where dimension index is a positive integer between 1 and 200, inclusive.
     * Impression List index must be a positive integer between 1 and 200, inclusive. Product index must be a positive
     * integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before
     * using this field.
     *
     * Example value: Member
     * Example usage: il1pi2cd3=Member
     */
    public QueryBuilder withIlXPiYCdZ (
        int listIndex,
        int productIndex,
        int dimensionIndex,
        String productImpressionCustomDimension
    ) {

        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);
        assertBetween("dimensionIndex", 1, 200, dimensionIndex);

        addParameter(
            MessageFormat.format(IL_X_PI_Y_CD_Z, listIndex, productIndex, dimensionIndex),
            productImpressionCustomDimension
        );

        return this;
    }

    public static final String IL_X_PI_Y_CM = "il{0}pi{1}cm{2}";

    /**
     * Product Impression Custom Metric
     *
     * Optional.
     *
     * A product-level custom metric where metric index is a positive integer between 1 and 200, inclusive. Impression
     * List index must be a positive integer between 1 and 200, inclusive. Product index must be a positive integer
     * between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
     * field.
     *
     * Example value: 28
     * Example usage: il1pi2cm3=28
     */
    public QueryBuilder withIlXPiYCmZ (
        int listIndex,
        int productIndex,
        int metricIndex,
        String productImpressionCustomMetric
    ) {
        assertBetween("listIndex", 1, 200, listIndex);
        assertBetween("productIndex", 1, 200, productIndex);
        assertBetween("metricIndex", 1, 200, metricIndex);

        addParameter(
            MessageFormat.format(IL_X_PI_Y_CM, listIndex, productIndex, metricIndex),
            productImpressionCustomMetric
        );

        return this;
    }

    public static final String PROMO_N_ID = "promo{0}id";

    /**
     * Promotion ID
     *
     * Optional.
     *
     * The promotion ID. Promotion index must be a positive integer between 1 and 200, inclusive. For analytics.js the
     * Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: SHIP
     * Example usage: promo1id=SHIP
     */
    public QueryBuilder withPromoNId (
        int promoIndex,
        String promotionID
    ) {
        assertBetween("promoIndex", 1, 200, promoIndex);

        addParameter(MessageFormat.format(PROMO_N_ID, promoIndex), promotionID);

        return this;
    }

    public static final String PROMO_N_NM = "promo{0}nm";

    /**
     * Promotion Name
     *
     * Optional.
     *
     * The name of the promotion. Promotion index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: Free Shipping
     * Example usage: promo1nm=Free%20Shipping
     */
    public QueryBuilder withPromoNNm (
        int promoIndex,
        String promotionName
    ) {
        assertBetween("promoIndex", 1, 200, promoIndex);

        addParameter(MessageFormat.format(PROMO_N_NM, promoIndex), promotionName);

        return this;
    }

    public static final String PROMO_N_CR = "promo{0}cr";

    /**
     * Promotion Creative
     *
     * Optional.
     *
     * The creative associated with the promotion. Promotion index must be a positive integer between 1 and 200,
     * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: Shipping Banner
     * Example usage: promo1cr=Shipping%20Banner
     */
    public QueryBuilder withPromoNCr (
        int promoIndex,
        String promotionCreative
    ) {
        assertBetween("promoIndex", 1, 200, promoIndex);

        addParameter(MessageFormat.format(PROMO_N_CR, promoIndex), promotionCreative);

        return this;
    }

    public static final String PROMO_N_PS = "promo{0}ps";

    /**
     * Promotion Position
     *
     * Optional.
     *
     * The position of the creative. Promotion index must be a positive integer between 1 and 200, inclusive. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: banner_slot_1
     * Example usage: promo1ps=banner_slot_1
     */
    public QueryBuilder withPromoNPs (
        int promoIndex,
        String promotionPosition
    ) {
        assertBetween("promoIndex", 1, 200, promoIndex);

        addParameter(MessageFormat.format(PROMO_N_PS, promoIndex), promotionPosition);

        return this;
    }

    public static final String PROMOA = "promoa";

    /**
     * Promotion Action
     *
     * Optional.
     *
     * Specifies the role of the promotions included in a hit. If a promotion action is not specified, the default
     * promotion action, 'view', is assumed. To measure a user click on a promotion set this to 'promo_click'. For
     * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
     *
     * Example value: click
     * Example usage: promoa=click
     */
    public QueryBuilder withPromoa (String promotionAction) {

        addParameter(PROMOA, promotionAction);

        return this;
    }

    public static final String SN = "sn";

    /**
     * Social Network
     *
     * Required for social hit type.
     *
     * Specifies the social network, for example Facebook or Google Plus.
     *
     * Example value: facebook
     * Example usage: sn=facebook
     */
    public QueryBuilder withSn (String socialNetwork) {

        checkSizeOf("socialNetwork", socialNetwork, 50);

        addParameter(SN, socialNetwork);

        return this;
    }

    public static final String SA = "sa";

    /**
     * Social Action
     *
     * Required for social hit type.
     *
     * Specifies the social interaction action. For example on Google Plus when a user clicks the +1 button, the social
     * action is 'plus'.
     *
     * Example value: like
     * Example usage: sa=like
     */
    public QueryBuilder withSa (String socialAction) {

        checkSizeOf("socialAction", socialAction, 50);

        addParameter(SA, socialAction);

        return this;
    }

    public static final String ST = "st";

    /**
     * Social Action Target
     *
     * Required for social hit type.
     *
     * Specifies the target of a social interaction. This value is typically a URL but can be any text.
     *
     * Example value: http://foo.com
     * Example usage: st=http%3A%2F%2Ffoo.com
     */
    public QueryBuilder withSt (String socialActionTarget) {

        checkSizeOf("socialActionTarget", socialActionTarget, 2048);

        addParameter(ST, socialActionTarget);

        return this;
    }

    public static final String UTC = "utc";

    /**
     * User timing category
     * 
     * Required for timing hit type.
     *
     * Specifies the user timing category.
     *
     * Example value: category
     * Example usage: utc=category
     */
    public QueryBuilder withUtc (String userTimingCategory) {

        checkSizeOf("userTimingCategory", userTimingCategory, 150);

        addParameter(UTC, userTimingCategory);

        return this;
    }

    public static final String UTV = "utv";

    /**
     * User timing variable name
     *
     * Required for timing hit type.
     *
     * Specifies the user timing variable.
     *
     * Example value: lookup
     * Example usage: utv=lookup
     */
    public QueryBuilder withUtv (String userTimingVariableName) {

        checkSizeOf("userTimingVariableName", userTimingVariableName, 500);

        addParameter(UTV, userTimingVariableName);

        return this;
    }

    public static final String UTT = "utt";

    /**
     * User timing time
     *
     * Required for timing hit type.
     *
     * Specifies the user timing value. The value is in milliseconds.
     *
     * Example value: 123
     * Example usage: utt=123
     */
    public QueryBuilder withUtt (long userTimingTime) {

        assertNotNegative("userTimingTime", userTimingTime);

        addParameter(UTT, userTimingTime);

        return this;
    }

    public static final String UTL = "utl";

    /**
     * User timing label
     *
     * Optional.
     *
     * Specifies the user timing label.
     *
     * Example value: label
     * Example usage: utl=label
     */
    public QueryBuilder withUtl (String userTimingLabel) {

        checkSizeOf("userTimingLabel", userTimingLabel, 500);

        addParameter(UTL, userTimingLabel);

        return this;
    }

    public static final String PLT = "plt";

    /**
     * Page Load Time
     *
     * Optional.
     *
     * Specifies the time it took for a page to load. The value is in milliseconds.
     *
     * Example value: 3554
     * Example usage: plt=3554
     */
    public QueryBuilder withPlt (long pageLoadTime) {

        assertNotNegative("pageLoadTime", pageLoadTime);

        addParameter("plt", pageLoadTime);

        return this;
    }

    public static final String DNS = "dns";

    /**
     * DNS Time
     *
     * Optional.
     *
     * Specifies the time it took to do a DNS lookup.The value is in milliseconds.
     *
     * Example value: 43
     * Example usage: dns=43
     */
    public QueryBuilder withDns (long dnsTime) {

        assertNotNegative("dnsTime", dnsTime);

        addParameter(DNS, dnsTime);

        return this;
    }

    public static final String PDT = "pdt";

    /**
     * Page Download Time
     *
     * Optional.
     *
     * Specifies the time it took for the page to be downloaded. The value is in milliseconds.
     *
     * Example value: 500
     * Example usage: pdt=500
     */
    public QueryBuilder withPdt (long pageDownloadTime) {

        assertNotNegative("pageDownloadTime", pageDownloadTime);

        addParameter(PDT, pageDownloadTime);

        return this;
    }

    public static final String RRT = "rrt";

    /**
     * Redirect Response Time
     *
     * Optional.
     *
     * Specifies the time it took for any redirects to happen. The value is in milliseconds.
     *
     * Example value: 500
     * Example usage: rrt=500
     */
    public QueryBuilder withRrt (long redirectResponseTime) {

        assertNotNegative("redirectResponseTime", redirectResponseTime);

        addParameter(RRT, redirectResponseTime);

        return this;
    }

    public static final String TCP = "tcp";

    /**
     * TCP Connect Time
     *
     * Optional.
     *
     * Specifies the time it took for a TCP connection to be made. The value is in milliseconds.
     *
     * Example value: 500
     * Example usage: tcp=500
     */
    public QueryBuilder withTcp (long tcpConnectTime) {

        assertNotNegative("tcpConnectTime", tcpConnectTime);

        addParameter(TCP, tcpConnectTime);

        return this;
    }

    public static final String SRT = "srt";

    /**
     * Server Response Time
     *
     * Optional.
     *
     * Specifies the time it took for the server to respond after the connect time. The value is in milliseconds.
     *
     * Example value: 500
     * Example usage: srt=500
     */
    public QueryBuilder withSrt (long serverResponseTime) {

        assertNotNegative("serverResponseTime", serverResponseTime);

        addParameter(SRT, serverResponseTime);

        return this;
    }

    public static final String DIT = "dit";

    /**
     * DOM Interactive Time
     *
     * Optional.
     *
     * Specifies the time it took for Document.readyState to be 'interactive'. The value is in milliseconds.
     *
     * Example value: 500
     * Example usage: dit=500
     */
    public QueryBuilder withDit (long domInteractiveTime) {

        assertNotNegative("domInteractiveTime", domInteractiveTime);

        addParameter(DIT, domInteractiveTime);

        return this;
    }

    public static final String CLT = "clt";

    /**
     * Content Load Time
     *
     * Optional.
     *
     * Specifies the time it took for the DOMContentLoaded Event to fire. The value is in milliseconds.
     *
     * Example value: 500
     * Example usage: clt=500
     */
    public QueryBuilder withClt (long contentLoadTime) {

        assertNotNegative("contentLoadTime", contentLoadTime);

        addParameter(CLT, contentLoadTime);

        return this;
    }

    public static final String EXD = "exd";

    /**
     * Exception Description
     *
     * Optional.
     *
     * Specifies the description of an exception.
     *
     * Example value: DatabaseError
     * Example usage: exd=DatabaseError
     */
    public QueryBuilder withExd (String exceptionDescription) {

        checkSizeOf("exceptionDescription", exceptionDescription, 150);

        addParameter(EXD, exceptionDescription);

        return this;
    }

    public static final String EXF = "exf";

    /**
     * Is Exception Fatal?
     *
     * Optional.
     *
     * Specifies whether the exception was fatal.
     *
     * Example value: 0
     * Example usage: exf=0
     */
    public QueryBuilder withExf (boolean exceptionFatal) {

        addParameter(EXF, asBoolean(exceptionFatal));

        return this;
    }

    public static final String CDX = "cd{0}";

    /**
     * Custom Dimension
     *
     * Optional.
     *
     * Each custom dimension has an associated index. There is a maximum of 20 custom dimensions (200 for Analytics 360
     * accounts). The dimension index must be a positive integer between 1 and 200, inclusive.
     *
     * Example value: Sports
     * Example usage: cd1=Sports
     */
    public QueryBuilder withCdX (int dimensionIndex, String customDimension) {

        assertBetween("dimensionIndex", 1, 200, dimensionIndex);

        addParameter(MessageFormat.format(CDX, dimensionIndex), customDimension);

        return this;
    }

    public static final String CMX = "cm{0}";

    /**
     * Custom Metric
     *
     * Optional.
     *
     * Each custom metric has an associated index. There is a maximum of 20 custom metrics (200 for Analytics 360
     * accounts). The metric index must be a positive integer between 1 and 200, inclusive.
     *
     * Example value: 47
     * Example usage: cm1=47
     */
    public QueryBuilder withCmX (int metricIndex, Number customMetric) {

        assertBetween("metricIndex", 1, 200, metricIndex);

        addParameter(MessageFormat.format(CMX, metricIndex), customMetric);

        return this;
    }

    public static final String XID = "xid";

    /**
     * Experiment ID
     *
     * Optional.
     *
     * This parameter specifies that this user has been exposed to an experiment with the given ID. It should be sent in
     * conjunction with the Experiment Variant parameter.
     *
     * Example value: Qp0gahJ3RAO3DJ18b0XoUQ
     * Example usage: xid=Qp0gahJ3RAO3DJ18b0XoUQ
     */
    public QueryBuilder withXid (String experimentID) {

        checkSizeOf("experimentID", experimentID, 40);

        addParameter(XID, experimentID);

        return this;
    }

    public static final String XVAR = "xvar";

    /**
     * Experiment Variant
     *
     * Optional.
     *
     * This parameter specifies that this user has been exposed to a particular variation of an experiment. It should be
     * sent in conjunction with the Experiment ID parameter.
     *
     * Example value: 1
     * Example usage: xvar=1
     */
    public QueryBuilder withXvar (String experimentVariant) {

        addParameter(XVAR, experimentVariant);

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

        ResponseEntity<T> responseEntity = (ResponseEntity<T>) response;

        if (!HttpStatus.OK.equals(responseEntity.getStatusCode()))
            throw new PostFailedException("The post failed for the URI " + uri +
                " (http status: " + responseEntity.getStatusCodeValue() + ")");

        return response.getBody();
    }

    public ResponseEntity<?> doPost () {
        return doGet(ResponseEntity.class);
    }
}
