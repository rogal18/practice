package com.sadapay.loggingutils;

import static com.sadapay.loggingutils.ConditionalOperationSink.bodyExtractor;
import static com.sadapay.loggingutils.ConditionalOperationSink.pathPredicate;
import static org.zalando.logbook.Conditions.exclude;
import static org.zalando.logbook.HeaderFilters.replaceHeaders;

import com.sadapay.loggingutils.filters.PartialReplaceBodyFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.BodyFilters;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HeaderFilters;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.Sink;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.json.JsonBodyFilters;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogbookBuilder {

  public static Logbook defaultLogbook() {
    return Logbook.builder().build();
  }


  public static NameBuilder publicAPIBuilder() {
    return name -> commonBuilder(LogbookConfiguration.initialCorrelationId(name));
  }

  public static NameBuilder internalCallBuilder() {
    return name -> commonBuilder(LogbookConfiguration.internalCorrelationId(name));
  }

  private static ConditionBuilder commonBuilder(CorrelationId correlationId) {
    return condition -> bodyFilter -> headerFilter -> sink -> strategy -> () ->
        Logbook.builder()
            .correlationId(correlationId)
            .condition(condition)
            .bodyFilter(bodyFilter)
            .headerFilter(headerFilter)
            .sink(sink)
            .strategy(strategy)
            .build();
  }

  public interface NameBuilder extends Function<String, ConditionBuilder> {

    default ConditionBuilder name(String name) {
      return apply(name);
    }

    default ConditionBuilder noName() {
      return apply(null);
    }
  }

  public interface ConditionBuilder extends Function<Predicate<HttpRequest>, BodyFilterBuilder> {

    default BodyFilterBuilder withCustomCondition(Predicate<HttpRequest> condition) {
      return apply(condition);
    }

    default BodyFilterBuilder withExcludePathCondition(Set<String> excludedPaths) {
      return apply(exclude(httpRequest -> excludedPaths.contains(httpRequest.getPath())));
    }

    default BodyFilterBuilder withExcludePathCondition(String[] excludedPaths) {
      return withExcludePathCondition(new HashSet<>(Arrays.asList((excludedPaths))));
    }

    default BodyFilterBuilder noCondition() {
      return apply(null);
    }
  }

  public interface BodyFilterBuilder extends Function<BodyFilter, HeaderFilterBuilder> {

    default HeaderFilterBuilder withReplaceBodyFilter(String[] obfuscatedFields) {
      return withReplaceBodyFilter("XXX", new HashSet<>(Arrays.asList((obfuscatedFields))));
    }

    default HeaderFilterBuilder withReplaceBodyFilter(String obfuscateWith, String[] obfuscatedFields) {
      return withReplaceBodyFilter(obfuscateWith, new HashSet<>(Arrays.asList((obfuscatedFields))));
    }

    default HeaderFilterBuilder withReplaceBodyFilter(String obfuscateWith, Set<String> obfuscatedFields) {
      return apply(BodyFilter.merge(BodyFilters.defaultValue(),
          JsonBodyFilters.replaceJsonStringProperty(obfuscatedFields, obfuscateWith)));
    }

    default HeaderFilterBuilder withPartialReplaceBodyFilter(String obfuscateWith, String[] obfuscatedFields) {
      HashSet<String> fields = new HashSet<>(Arrays.asList(obfuscatedFields));
      return apply(PartialReplaceBodyFilter.of(obfuscateWith, fields));
    }

    default HeaderFilterBuilder withCustomBodyFilter(BodyFilter bodyFilter) {
      return apply(bodyFilter);
    }

    default HeaderFilterBuilder noBodyFilters() {
      return apply(BodyFilter.none());
    }

    default SinkBuilder noFilters() {
      return apply(BodyFilter.none()).apply(HeaderFilter.none());
    }
  }

  public interface HeaderFilterBuilder extends Function<HeaderFilter, SinkBuilder> {

    default SinkBuilder withReplaceHeaderFilter(String obfuscateWith, Set<String> obfuscatedHeaders) {
      return apply(HeaderFilter.merge(
          HeaderFilters.defaultValue(),
          replaceHeaders(obfuscatedHeaders, obfuscateWith)));
    }

    default SinkBuilder withReplaceHeaderFilter(String[] obfuscatedHeaders) {
      return withReplaceHeaderFilter("XXX", obfuscatedHeaders);
    }

    default SinkBuilder withReplaceHeaderFilter(String obfuscateWith, String[] obfuscatedHeaders) {
      return withReplaceHeaderFilter(obfuscateWith, new HashSet<>(Arrays.asList(obfuscatedHeaders)));
    }

    default SinkBuilder withCustomHeaderFilter(HeaderFilter headerFilter) {
      return apply(headerFilter);
    }

    default SinkBuilder noHeaderFilters() {
      return apply(HeaderFilter.none());
    }

  }

  public interface SinkBuilder extends Function<Sink, StrategyBuilder> {

    default StrategyBuilder customSink(Sink sink) {
      return apply(sink);
    }

    default StrategyBuilder conditionalPathOperationSink(Predicate<String> pathPredicate,
        BiConsumer<String, String> requestResponseBodyConsumer) {
      return apply(ConditionalOperationSink.of(
          LogbookConfiguration.jsonAndDebugSink,
          Set.of(Pair.of(
              pathPredicate(pathPredicate),
              (request, response) -> requestResponseBodyConsumer.accept(bodyExtractor(request), bodyExtractor(response))
              )
          )
      ));
    }

    default StrategyBuilder jsonAndDebugSink() {
      return apply(LogbookConfiguration.jsonAndDebugSink);
    }

    default StrategyBuilder defaultSink() {
      return apply(null);
    }

  }

  public interface StrategyBuilder extends Function<Strategy, Build> {

    default Build customSink(Strategy sink) {
      return apply(sink);
    }

    default Build defaultStrategy() {
      return apply(null);
    }

  }

  public interface Build extends Supplier<Logbook> {

    default Logbook build() {
      return get();
    }
  }
}
