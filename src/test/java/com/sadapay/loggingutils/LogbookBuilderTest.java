package com.sadapay.loggingutils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.zalando.logbook.Logbook;

//todo: add integration test with Log4j2TestHelper and test sample request response logs
class LogbookBuilderTest {

  @Test
  void shouldCreateInternaclCallLogbookInstance() {
    Logbook build = LogbookBuilder.internalCallBuilder()
        .name("iris")
        .noCondition()
        .withPartialReplaceBodyFilter("XXX", new String[]{"test"})
        .withReplaceHeaderFilter("XXX", new String[]{"test"})
        .jsonAndDebugSink()
        .defaultStrategy()
        .build();

    assertThat(build).isNotNull();
  }

  @Test
  void shouldCreatePublicAPILogbookInstance() {
    Logbook build = LogbookBuilder.publicAPIBuilder()
        .name("iris")
        .noCondition()
        .withPartialReplaceBodyFilter("XXX", new String[]{"test"})
        .withReplaceHeaderFilter("XXX", new String[]{"test"})
        .jsonAndDebugSink()
        .defaultStrategy()
        .build();

    assertThat(build).isNotNull();
  }

  @Test
  void shouldCreateDefaultLogbookInstance() {
    Logbook build = LogbookBuilder.defaultLogbook();

    assertThat(build).isNotNull();
  }
}