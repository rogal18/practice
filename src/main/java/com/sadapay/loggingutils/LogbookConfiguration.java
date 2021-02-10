package com.sadapay.loggingutils;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Sink;
import org.zalando.logbook.json.JsonHttpLogFormatter;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
class LogbookConfiguration {

  private static final Supplier<String> mdcRequestIdSupplier = new MdcRequestIdSupplier();

  public static final Sink jsonAndDebugSink = new DefaultSink(
      new JsonHttpLogFormatter(),
      new DebugHttpLogWriter());


  public static CorrelationId initialCorrelationId(String systemName) {
    return request -> {
      MDC.put("initialHttpMethod", request.getMethod());
      MDC.put("initialHttpPath", request.getPath());
      MDC.put("initialSystem", systemName);
      MDC.put("httpMethod", MDC.get("initialHttpMethod"));
      MDC.put("httpPath", MDC.get("initialHttpPath"));
      MDC.put("system", MDC.get("initialSystem"));
      return mdcRequestIdSupplier.get();
    };
  }

  public static CorrelationId internalCorrelationId(String systemName) {
    return request -> {
      MDC.put("httpMethod", request.getMethod());
      MDC.put("httpPath", request.getPath());
      MDC.put("system", systemName);
      return mdcRequestIdSupplier.get();
    };
  }

}
