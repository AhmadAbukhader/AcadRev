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
public class DocumentMetadataDto {
    private Integer id;
    private String fileName;
    private String fileType;
    private String documentType;
    private LocalDateTime uploadedAt;
    private Integer companyId;
    private String companyName;
}
