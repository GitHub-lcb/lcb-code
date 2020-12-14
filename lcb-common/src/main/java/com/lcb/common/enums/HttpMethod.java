package com.lcb.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;

/**
 * 请求方式
 *
 * @author lcb
 */
public enum HttpMethod {
  GET,
  HEAD,
  POST,
  PUT,
  PATCH,
  DELETE,
  OPTIONS,
  TRACE;

  private static final Map<String, HttpMethod> mappings = new HashMap<>(16);

  static {
    for (com.lcb.common.enums.HttpMethod httpMethod : values()) {
      mappings.put(httpMethod.name(), httpMethod);
    }
  }

  @Nullable
  public static com.lcb.common.enums.HttpMethod resolve(@Nullable String method) {
    return (method != null ? mappings.get(method) : null);
  }

  public boolean matches(String method) {
    return (this == resolve(method));
  }
}
