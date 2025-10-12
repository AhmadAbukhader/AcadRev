package com.AcadRev.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "documentId", nullable = false)
    private Document document;

    @ManyToOne
    @JoinColumn(name = "auditorId", nullable = false)
    private User auditor;

    @Column(nullable = false)
    private Integer rating;

    private String comments;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt = LocalDateTime.now();


}
