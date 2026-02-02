package com.stackdarker.platform.notification.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MdcEnrichmentFilter extends OncePerRequestFilter {

    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_USER_ID = "userId";

    public static final String HDR_REQUEST_ID = "X-Request-Id";
    public static final String HDR_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = trimToNull(request.getHeader(HDR_REQUEST_ID));
        String traceId = trimToNull(request.getHeader(HDR_TRACE_ID));
        String userId = extractUserIdFromSecurityContext();

        if (requestId != null) MDC.put(MDC_REQUEST_ID, requestId);
        if (traceId != null) MDC.put(MDC_TRACE_ID, traceId);
        if (userId != null) MDC.put(MDC_USER_ID, userId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_ID);
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_USER_ID);
        }
    }

    private String extractUserIdFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UUID uuid) return uuid.toString();

        String name = trimToNull(auth.getName());
        if (name == null) return null;

        try {
            return UUID.fromString(name).toString();
        } catch (Exception ignored) {
            return name;
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
