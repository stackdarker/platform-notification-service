package com.stackdarker.platform.notification.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackdarker.platform.notification.api.error.ErrorItem;
import com.stackdarker.platform.notification.api.error.ErrorResponse;
import com.stackdarker.platform.notification.observability.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String requestId = request.getHeader(RequestIdFilter.HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            Object attr = request.getAttribute(RequestIdFilter.ATTR_REQUEST_ID);
            requestId = attr == null ? null : attr.toString();
        }

        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(Instant.now());
        body.setStatus(HttpStatus.UNAUTHORIZED.value());
        body.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.setCode("AUTH_UNAUTHORIZED");
        body.setMessage("Authentication required.");
        body.setPath(request.getRequestURI());
        body.setRequestId(requestId);
        body.setErrors(List.of(new ErrorItem("UNAUTHORIZED", "Missing, invalid, or expired token.")));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
