package com.sadapay.loggingutils;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.Test;

class LogbookHttpClientTest {


  @Test
  void shouldBuildLogbookHttpClients() {
    HttpClient httpClient = LogbookHttpClient.build(LogbookBuilder.defaultLogbook());
    assertThat(httpClient).isNotNull();
  }
}
