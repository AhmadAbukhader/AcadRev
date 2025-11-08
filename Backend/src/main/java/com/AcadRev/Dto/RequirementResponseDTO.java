package com.AcadRev.Dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequirementResponseDTO {
    private int id;
    private int requirementId;
    private int companyId;
    private String responseText;
    private Integer parentResponseId;
    private Integer createdByUserId;
    private String createdByUserName;
    private String createdByUserRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RequirementResponseDTO> replies; // Nested replies
}
