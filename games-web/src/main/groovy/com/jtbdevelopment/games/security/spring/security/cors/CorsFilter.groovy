package com.jtbdevelopment.games.security.spring.security.cors

import groovy.transform.CompileStatic

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Date: 8/27/15
 * Time: 4:50 PM
 */
@CompileStatic
class CorsFilter implements Filter {
    private CorsHeaderWriter headerWriter = new CorsHeaderWriter()

    @Override
    void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    void doFilter(
            final ServletRequest request,
            final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request
            HttpServletResponse httpServletResponse = (HttpServletResponse) response

            if (httpServletRequest.getMethod() == "OPTIONS") {
                headerWriter.writeHeaders(httpServletRequest, httpServletResponse)
                httpServletResponse.setStatus(HttpServletResponse.SC_OK)
            } else {
                chain.doFilter(request, response)
            }
        }
    }

    @Override
    void destroy() {

    }
}
