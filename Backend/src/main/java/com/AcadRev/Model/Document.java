package com.AcadRev.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_data", nullable = false, columnDefinition = "bytea")
    private byte[] fileData;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "document_type")
    private String documentType;

    @Builder.Default
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "requirement_id")
    private Requirement requirement;

}
