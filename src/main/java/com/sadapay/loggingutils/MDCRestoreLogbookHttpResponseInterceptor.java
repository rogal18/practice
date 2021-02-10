package com.sadapay.loggingutils;

import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.MDC;
import org.zalando.logbook.httpclient.LogbookHttpResponseInterceptor;

/**
 * This class wraps LogbookHttpResponseInterceptor and additionally restores MDC fields to initial values. Internal
 * calls overwrite httpMethod and httpPath MDC fields with new values that needs to be restored when call processing is
 * finished.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MDCRestoreLogbookHttpResponseInterceptor implements HttpResponseInterceptor {

  private final LogbookHttpResponseInterceptor logbookHttpResponseInterceptor;

  public void process(final HttpResponse original, final HttpContext context) throws IOException {
    logbookHttpResponseInterceptor.process(original, context);
    MDC.put("httpMethod", MDC.get("initialHttpMethod"));
    MDC.put("httpPath", MDC.get("initialHttpPath"));
    MDC.put("system", MDC.get("initialSystem"));

  }
}
