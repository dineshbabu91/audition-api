package com.audition.configuration;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@WebFilter("/*")
@Component
public class ResponseHeaderInjector implements Filter {
    private final Tracer tracer;

    public ResponseHeaderInjector(final Tracer tracer) {
        this.tracer = tracer;
    }

    public String getTraceId() {
        return Optional.of(tracer).map(Tracer::currentTraceContext).map(CurrentTraceContext::context).map(TraceContext::traceId).orElse("");
    }

    public String getSpanId() {
        return Optional.of(tracer).map(Tracer::currentTraceContext).map(CurrentTraceContext::context).map(TraceContext::spanId).orElse("");
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader("traceId", getTraceId());
        httpServletResponse.setHeader("spanID", getSpanId());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
