package com.audition.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class ApiHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
        final Object handler) {
        final long startTimeStamp = Instant.now().toEpochMilli();
        request.setAttribute("startTimeStamp", startTimeStamp);
        final String requestLogging = String.format("Api Transaction start timestamp: %s Method: %s Path: %s",
            startTimeStamp,
            request.getMethod(),
            request.getRequestURI());
        log.info(requestLogging);
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
        final Object handler, @Nullable final Exception ex) {
        final long endTimeStamp = Instant.now().toEpochMilli();
        final String responseLogging = String.format(
            "Api Transaction End timestamp: %s Method: %s, Path: %s duration: %s status: %s",
            endTimeStamp,
            request.getMethod(),
            request.getRequestURI(),
            endTimeStamp - (long) request.getAttribute("startTimeStamp"),
            response.getStatus()
        );
        log.info(responseLogging);
    }
}
