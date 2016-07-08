package org.openlmis.web.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LargeRequestFilter extends OncePerRequestFilter {

    public static final int MAX_REQUEST_SIZE = 5242880;  // 5 MB

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getContentLength() > MAX_REQUEST_SIZE) {
            response.reset();
            response.sendError(HttpStatus.FORBIDDEN.value(), "Request size too larger!");
            return;
        }
        filterChain.doFilter(request, response);
    }

}
