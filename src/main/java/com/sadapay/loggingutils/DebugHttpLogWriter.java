package com.sadapay.loggingutils;

import lombok.extern.slf4j.Slf4j;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

@Slf4j
public class DebugHttpLogWriter implements HttpLogWriter {

  @Override
  public boolean isActive() {
    return log.isDebugEnabled();
  }

  @Override
  public void write(Precorrelation precorrelation, String request) {
    log.debug(request);
  }

  @Override
  public void write(Correlation correlation, String response) {
    log.debug(response);
  }
}
