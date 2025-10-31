package com.AcadRev.Dto;

import com.AcadRev.Model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private String id;
        private String username;
        private String name;
        private String role;
    }
}
