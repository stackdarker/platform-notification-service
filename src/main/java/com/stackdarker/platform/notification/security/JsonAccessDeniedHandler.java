package com.stackdarker.platform.notification.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackdarker.platform.notification.api.error.ErrorItem;
import com.stackdarker.platform.notification.api.error.ErrorResponse;
import com.stackdarker.platform.notification.observability.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JsonAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        String requestId = request.getHeader(RequestIdFilter.HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            Object attr = request.getAttribute(RequestIdFilter.ATTR_REQUEST_ID);
            requestId = attr == null ? null : attr.toString();
        }

        ErrorResponse body = new ErrorResponse();
        body.setTimestamp(Instant.now());
        body.setStatus(HttpStatus.FORBIDDEN.value());
        body.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        body.setCode("AUTH_FORBIDDEN");
        body.setMessage("You do not have permission to access this resource.");
        body.setPath(request.getRequestURI());
        body.setRequestId(requestId);
        body.setErrors(List.of(new ErrorItem("FORBIDDEN", "Insufficient privileges.")));

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
