package com.jtbdevelopment.games.security.spring.security.cors;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date: 8/27/15 Time: 4:50 PM
 *
 * Primarily for testing locally and should not normally be used
 *
 * No tests for same reason
 */
public class CorsFilter implements Filter {

  private CorsHeaderWriter headerWriter = new CorsHeaderWriter();

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response,
      final FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;

      if (httpServletRequest.getMethod().equals("OPTIONS")) {
        headerWriter.writeHeaders(httpServletRequest, httpServletResponse);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
      } else {
        chain.doFilter(request, response);
      }

    }

  }

  @Override
  public void destroy() {

  }
}
