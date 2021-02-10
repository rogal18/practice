package com.sadapay.loggingutils;

import java.io.IOException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpMessage;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

/**
 * Works like DefaultSink but also additional BiConsumer for request and response can be added based on predicate
 */
@Slf4j
@RequiredArgsConstructor
public class ConditionalOperationSink implements Sink {

  private final Sink defaultSink;
  private final Set<Pair<Predicate<HttpRequest>, BiConsumer<HttpRequest, HttpResponse>>> conditionalOperations;

  public static Sink of(
      Sink defaultSink,
      Set<Pair<Predicate<HttpRequest>, BiConsumer<HttpRequest, HttpResponse>>> conditionalOperations) {
    return new ConditionalOperationSink(defaultSink, conditionalOperations);
  }

  @Override
  public boolean isActive() {
    return this.defaultSink.isActive();
  }

  @Override
  public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
    this.defaultSink.write(precorrelation, request);
  }

  @Override
  public void write(Correlation correlation, HttpRequest request, HttpResponse response)
      throws IOException {
    this.defaultSink.write(correlation, request, response);

    conditionalOperations.forEach(pair -> {
      if (pair.getLeft().test(request)) {
        pair.getRight().accept(request, response);
      }
    });
  }

  public static Predicate<HttpRequest> pathContains(String pathPart) {
    return httpRequest -> httpRequest.getPath().contains(pathPart);
  }

  public static Predicate<HttpRequest> pathPredicate(Predicate<String> pathPredicate) {
    return httpRequest -> {
      String path = httpRequest.getPath();
      return pathPredicate.test(path);
    };
  }

  public static String bodyExtractor(HttpMessage httpMessage) {
    try {
      return httpMessage.getBodyAsString();
    } catch (IOException e) {
      log.warn("Unable to extract body for logging purposes", e);
      throw new IllegalStateException("Unable to extract body for logging purposes");
    }
  }
}
