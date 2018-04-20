package com.jtbdevelopment.games.security.spring.security.cachecontrol;

import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.header.HeaderWriter;

/**
 * Date: 6/8/15 Time: 6:38 AM
 */
public class SmarterCacheControlHeaderWriter implements HeaderWriter {

  private static final String EXPIRES = "Expires";
  private static final String CACHE_CONTROL = "Cache-Control";
  private static final String PRAGMA = "Pragma";

  private static boolean allowCaching(final HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/images") || path.startsWith("/scripts") || path.startsWith("/styles")
        || path.startsWith("/views") || path.startsWith("/bower_components");
  }

  @Override
  public void writeHeaders(final HttpServletRequest request, final HttpServletResponse response) {
    if (allowCaching(request)) {
      response.addHeader(EXPIRES, "" + Instant.now().plusSeconds(60 * 60).getEpochSecond());
    } else {
      response.addHeader(CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
      response.addHeader(PRAGMA, "no-cache");
      response.addHeader(EXPIRES, "0");
    }

  }
}
