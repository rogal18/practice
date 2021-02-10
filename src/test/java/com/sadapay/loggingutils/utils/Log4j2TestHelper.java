package com.sadapay.loggingutils.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class Log4j2TestHelper {

  @Mock
  private Appender appender;

  private final String loggerName;

  public Log4j2TestHelper(String loggerName) {
    this.loggerName = loggerName;
  }

  public void setLevel(Level level) {
    Logger logger = LoggerContext.getContext(false).getLogger(loggerName);
    logger.setLevel(level);
  }

  public void setUp() {
    MockitoAnnotations.initMocks(this);

    when(appender.getName()).thenReturn("mock-appender");
    lenient().when(appender.isStarted()).thenReturn(true);

    Logger logger = LoggerContext.getContext(false).getLogger(loggerName);
    logger.addAppender(appender);
  }

  public void tearDown() {
    Logger logger = LoggerContext.getContext(false).getLogger(loggerName);
    logger.removeAppender(appender);
  }

  public void assertLog(Level level, String message) {
    ArgumentCaptor<LogEvent> argumentCaptor = ArgumentCaptor.forClass(LogEvent.class);
    verify(appender, atLeastOnce()).append(argumentCaptor.capture());

    List<LogEvent> allValues = argumentCaptor.getAllValues();
    List<LogEvent> actualEvents = allValues
        .parallelStream()
        .filter(e -> e.getMessage().getFormattedMessage().equals(message))
        .collect(Collectors.toList());

    assertEquals(1, actualEvents.size(), logEventsMessage(allValues, message));
    assertEquals(level, actualEvents.get(0).getLevel());
    assertEquals(message, actualEvents.get(0).getMessage().getFormattedMessage());
  }

  public void verifyNoInteractions() {
    Mockito.verify(appender, times(0)).append(any());
  }

  private static String logEventsMessage(List<LogEvent> logEvents, String expectedMessage) {
    StringBuilder builder = new StringBuilder();
    if (logEvents.size() > 0) {
      for (LogEvent event : logEvents) {
        builder.append("\n");
        builder.append(event.toString());
      }

    } else {
      builder.append("[]");
    }

    return "Expected \"" + expectedMessage + "\", instead got: " + builder.toString() + "\n";
  }
}
