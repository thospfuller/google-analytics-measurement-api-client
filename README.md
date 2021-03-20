# Introduction Google Analytics Measurement API Client

A Java API for working with the Google Measurement API.

# Google Analytics Measurement API Client Examples

[![Google Analytics Measurement API: How to invoke from Java](https://youtu.be/vyuY3ZLxlAQ/hqdefault.jpg)](https://youtu.be/vyuY3ZLxlAQ)

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
