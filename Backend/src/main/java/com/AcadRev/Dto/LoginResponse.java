package com.AcadRev.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private long expiresIn;
    private UserInfo user;

    @Data
    @Builder
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String username;
        private String name;
        private String role;
        private Integer companyId;
    }
}
