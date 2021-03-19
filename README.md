# Introduction Google Analytics Measurement API Client

A Java API for working with the Google Measurement API.

https://youtu.be/vyuY3ZLxlAQ

# Google Analytics Measurement API Client Examples

```java
queryBuilder
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
```

# See Also

- https://developers.google.com/analytics/devguides/collection/protocol/v1/
