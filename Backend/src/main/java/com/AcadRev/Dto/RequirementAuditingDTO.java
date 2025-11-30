package com.AcadRev.Dto;

import lombok.Data;

@Data
public class RequirementAuditingDTO {
    private int id;
    private int requirementId;
    private int companyId;
    private int auditorId;
    private int status;
}
