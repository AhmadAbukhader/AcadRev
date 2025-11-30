package com.AcadRev.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyProfileDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("industry")
    private String industry;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;

}
