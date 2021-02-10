package com.sadapay.loggingutils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.slf4j.MDC;

class MdcRequestIdSupplier implements Supplier<String> {

  public static final String REQUEST_ID = "requestId";

  @Override
  public String get() {
    return Optional.ofNullable(MDC.get(REQUEST_ID)).orElseGet(() -> UUID.randomUUID().toString());
  }
}
