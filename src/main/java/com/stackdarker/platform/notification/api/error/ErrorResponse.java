package com.stackdarker.platform.notification.api.error;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {

    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private String requestId;
    private List<ErrorItem> errors;

    public ErrorResponse() {}

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public List<ErrorItem> getErrors() { return errors; }
    public void setErrors(List<ErrorItem> errors) { this.errors = errors; }
}
