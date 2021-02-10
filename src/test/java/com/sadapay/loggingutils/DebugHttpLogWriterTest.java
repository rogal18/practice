package com.sadapay.loggingutils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.Precorrelation;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DebugHttpLogWriterTest {

  private DebugHttpLogWriter debugHttpLogWriter;

  @Mock
  private Precorrelation precorrelation;

  @Mock
  private Correlation correlation;

  @BeforeEach
  void setUp() {
    debugHttpLogWriter = new DebugHttpLogWriter();
  }

  @Test
  void shouldCheckIfActive() {
    assertThat(debugHttpLogWriter.isActive()).isEqualTo(log.isDebugEnabled());
  }

  @Test
  void shouldLogRequest() {
    //todo: use Log4j2TestHelper to test it better
    assertAll(() ->
        debugHttpLogWriter.write(precorrelation, "request")
    );
  }

  @Test
  void shouldLogResponse() {
    //todo: use Log4j2TestHelper to test it better
    assertAll(() ->
        debugHttpLogWriter.write(correlation, "request")
    );
  }
}