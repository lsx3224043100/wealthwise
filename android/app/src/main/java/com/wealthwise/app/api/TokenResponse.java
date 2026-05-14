package com.wealthwise.app.api;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("username")
    private String username;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
