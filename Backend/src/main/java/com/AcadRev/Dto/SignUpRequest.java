package com.AcadRev.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SignUpRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;

    @JsonProperty("role")
    private String role;

    @JsonProperty("companyId")
    private Integer companyId;

}
