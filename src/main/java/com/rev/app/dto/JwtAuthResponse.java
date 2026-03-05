package com.rev.app.dto;

/**
 * Response body returned by POST /api/auth/login on successful authentication.
 */
public class JwtAuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private Long employeeId;
    private String email;
    private String role;
    private String firstName;

    public JwtAuthResponse() {
    }

    public JwtAuthResponse(String token, long expiresInMs,
            Long employeeId, String email,
            String role, String firstName) {
        this.token = token;
        this.expiresInMs = expiresInMs;
        this.employeeId = employeeId;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
    }

    // ---- Getters & Setters ----

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
