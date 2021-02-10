package com.sadapay.loggingutils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.zalando.logbook.httpclient.LogbookHttpResponseInterceptor;

@ExtendWith(MockitoExtension.class)
class MDCRestoreLogbookHttpResponseInterceptorTest {

  @Mock
  private HttpResponse httpResponse;

  @Mock
  private HttpContext httpContext;

  @Mock
  private LogbookHttpResponseInterceptor logbookHttpResponseInterceptor;

  private MDCRestoreLogbookHttpResponseInterceptor mdcRestoreLogbookHttpResponseInterceptor;

  @BeforeEach
  void setUp() {
    mdcRestoreLogbookHttpResponseInterceptor = new MDCRestoreLogbookHttpResponseInterceptor(
        logbookHttpResponseInterceptor);
  }

  @Test
  void shouldPassProcessingToWrappedClassAndRestoreMDCContext() throws IOException {
    MDC.put("initialHttpMethod", "initialHttpMethod");
    MDC.put("initialHttpPath", "initialHttpPath");
    MDC.put("initialSystem", "initialSystem");
    MDC.put("httpMethod", "httpMethod");
    MDC.put("httpPath", "httpPath");
    MDC.put("system", "system");

    mdcRestoreLogbookHttpResponseInterceptor.process(httpResponse, httpContext);

    assertThat(MDC.get("httpMethod")).isEqualTo("initialHttpMethod");
    assertThat(MDC.get("httpPath")).isEqualTo("initialHttpPath");
    assertThat(MDC.get("system")).isEqualTo("initialSystem");

    verify(logbookHttpResponseInterceptor).process(httpResponse, httpContext);
  }
}