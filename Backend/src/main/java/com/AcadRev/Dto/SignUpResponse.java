package com.AcadRev.Dto;

import com.AcadRev.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SignUpResponse {
    private String token;
    private String username;
    private Role role;
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
