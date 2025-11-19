package com.AcadRev.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditorAssignmentDto {
    private Integer id;
    private Integer companyProfileId;
    private String companyName;
    private Integer externalAuditorId;
    private String externalAuditorName;
    private String externalAuditorUsername;
    private Integer assignedByInternalAuditorId;
    private String assignedByInternalAuditorName;
    private LocalDateTime assignedAt;
    private Boolean isActive;
}

