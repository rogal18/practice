package com.sadapay.loggingutils;

import static com.sadapay.loggingutils.LogbookConfiguration.initialCorrelationId;
import static com.sadapay.loggingutils.LogbookConfiguration.internalCorrelationId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.zalando.logbook.HttpRequest;



@ExtendWith(MockitoExtension.class)
class LogbookConfigurationTest {

  @Mock
  private HttpRequest httpRequest;

  @BeforeEach
  void setUp() {
    when(httpRequest.getMethod()).thenReturn("GET");
    when(httpRequest.getPath()).thenReturn("/url");
  }

  @Test
  void shouldSetInitialFields() {
    when(httpRequest.getMethod()).thenReturn("GET");
    when(httpRequest.getPath()).thenReturn("/url");

    initialCorrelationId("name").generate(httpRequest);
    assertThat(MDC.get("initialHttpMethod")).isEqualTo("GET");
    assertThat(MDC.get("initialHttpPath")).isEqualTo("/url");
    assertThat(MDC.get("initialSystem")).isEqualTo("name");
    assertThat(MDC.get("httpMethod")).isEqualTo("GET");
    assertThat(MDC.get("httpPath")).isEqualTo("/url");
    assertThat(MDC.get("system")).isEqualTo("name");
  }

  @Test
  void shouldSetInternalFields() {
    when(httpRequest.getMethod()).thenReturn("POST");
    when(httpRequest.getPath()).thenReturn("/url2");

    internalCorrelationId("otherName").generate(httpRequest);
    assertThat(MDC.get("httpMethod")).isEqualTo("POST");
    assertThat(MDC.get("httpPath")).isEqualTo("/url2");
    assertThat(MDC.get("system")).isEqualTo("otherName");
  }
}
