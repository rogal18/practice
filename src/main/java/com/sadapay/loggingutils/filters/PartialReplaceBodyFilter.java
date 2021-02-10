package com.sadapay.loggingutils.filters;

import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.BodyFilters;
import org.zalando.logbook.json.JsonBodyFilters;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartialReplaceBodyFilter {

  public static final int NON_OBFUSCATED_PORTION = 4;

  public static BodyFilter of(Set<String> fields) {
    return of("XXX", fields);
  }

  public static BodyFilter of(String obfuscateWith, Set<String> fields) {
    return BodyFilter.merge(
        BodyFilters.defaultValue(),
        JsonBodyFilters.replacePrimitiveJsonProperty(
            fields::contains,
            (name, value) -> {
              String unquoted = value.replace("\"", "");
              return obfuscateWith + unquoted
                  .substring(unquoted.length() - (unquoted.length() / NON_OBFUSCATED_PORTION));
            }
        ));
  }
}
