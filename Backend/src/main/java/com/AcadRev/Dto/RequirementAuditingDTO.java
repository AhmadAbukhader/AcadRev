package com.AcadRev.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequirementAuditingDTO {
    private int id;
    private int requirementId;
    private int companyId;
    private int auditorId;
    private int status;
}
