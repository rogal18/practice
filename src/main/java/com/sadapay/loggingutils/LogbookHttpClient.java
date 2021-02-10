package com.sadapay.loggingutils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.httpclient.LogbookHttpRequestInterceptor;
import org.zalando.logbook.httpclient.LogbookHttpResponseInterceptor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogbookHttpClient {

  public static HttpClient build(Logbook logbook) {
    return HttpClients.custom()
        .addInterceptorFirst(new LogbookHttpRequestInterceptor(logbook))
        .addInterceptorLast(new MDCRestoreLogbookHttpResponseInterceptor(new LogbookHttpResponseInterceptor()))
        .build();
  }
}
