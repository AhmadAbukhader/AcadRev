package com.AcadRev.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RequirementResponseDTO {
    private int id;
    private int requirementId;
    private int companyId;
    private String responseText;
}
