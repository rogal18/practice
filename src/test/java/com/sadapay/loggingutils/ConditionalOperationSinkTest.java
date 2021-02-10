package com.sadapay.loggingutils;

import static com.sadapay.loggingutils.ConditionalOperationSink.pathContains;
import static com.sadapay.loggingutils.ConditionalOperationSink.pathPredicate;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

@ExtendWith(MockitoExtension.class)
class ConditionalOperationSinkTest {

  @Mock
  private HttpLogFormatter formatter;

  @Mock
  private HttpLogWriter writer;

  @Mock
  private Set<Pair<Predicate<HttpRequest>, BiConsumer<HttpRequest, HttpResponse>>> conditionalOperations;

  private ConditionalOperationSink sink;

  @Mock
  private Correlation correlation;

  @Mock
  private Precorrelation precorrelation;

  @Mock
  private HttpRequest httpRequest;

  @Mock
  private HttpResponse httpResponse;


  @BeforeEach
  void setUp() {
    sink = new ConditionalOperationSink(new DefaultSink(formatter, writer), conditionalOperations);
  }

  @Test
  void shouldWriteRequest() throws IOException {
    when(formatter.format(any(Precorrelation.class), any(HttpRequest.class))).thenReturn("formatted");
    sink.write(precorrelation, httpRequest);

    verify(writer).write(precorrelation, "formatted");
  }

  @Test
  void shouldWriteResponse() throws IOException {
    when(formatter.format(any(Correlation.class), any(HttpResponse.class))).thenReturn("formatted");
    sink.write(correlation, httpRequest, httpResponse);

    verify(writer).write(correlation, "formatted");
  }

  @Test
  void shouldPerformAdditionalOperationsWithResponse() throws IOException {
    when(formatter.format(any(Correlation.class), any(HttpResponse.class))).thenReturn("formatted");

    CountDownLatch latch = new CountDownLatch(2);

    Set<Pair<Predicate<HttpRequest>, BiConsumer<HttpRequest, HttpResponse>>> ops = Set.of(
        Pair.of(p -> true, (a, b) -> latch.countDown()),
        Pair.of(p -> false, (a, b) -> latch.countDown()),
        Pair.of(p -> false, (a, b) -> latch.countDown()),
        Pair.of(p -> true, (a, b) -> latch.countDown())
    );

    ConditionalOperationSink sink = new ConditionalOperationSink(new DefaultSink(formatter, writer), ops);
    sink.write(correlation, httpRequest, httpResponse);

    if (latch.getCount() != 0) {
      fail();
    }
  }

  @Test
  void shouldPrepareCorrectPathContainsPredicate() throws IOException {
    when(formatter.format(any(Correlation.class), any(HttpResponse.class))).thenReturn("formatted");
    when(httpRequest.getPath()).thenReturn("path");

    CountDownLatch latch = new CountDownLatch(2);

    Set<Pair<Predicate<HttpRequest>, BiConsumer<HttpRequest, HttpResponse>>> ops = Set.of(
        Pair.of(pathContains("path"), (a, b) -> latch.countDown()),
        Pair.of(pathContains("p"), (a, b) -> latch.countDown()),
        Pair.of(pathContains("path_and_more"), (a, b) -> latch.countDown())
    );

    ConditionalOperationSink sink = new ConditionalOperationSink(new DefaultSink(formatter, writer), ops);
    sink.write(correlation, httpRequest, httpResponse);

    if (latch.getCount() != 0) {
      fail();
    }
  }

  @Test
  void shouldPrepareCorrectPathPredicate() throws IOException {
    when(formatter.format(any(Correlation.class), any(HttpResponse.class))).thenReturn("formatted");
    when(httpRequest.getPath()).thenReturn("path");

    CountDownLatch latch = new CountDownLatch(2);

    Set<Pair<Predicate<HttpRequest>, BiConsumer<HttpRequest, HttpResponse>>> ops = Set.of(
        Pair.of(pathPredicate(s -> s.contains("path")), (a, b) -> latch.countDown()),
        Pair.of(pathPredicate(s -> s.contains("p")), (a, b) -> latch.countDown()),
        Pair.of(pathPredicate(s -> s.contains("path_and_more")), (a, b) -> latch.countDown())
    );

    ConditionalOperationSink sink = new ConditionalOperationSink(new DefaultSink(formatter, writer), ops);
    sink.write(correlation, httpRequest, httpResponse);

    if (latch.getCount() != 0) {
      fail();
    }
  }
}