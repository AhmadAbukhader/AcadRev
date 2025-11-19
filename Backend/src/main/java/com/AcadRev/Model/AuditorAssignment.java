package com.AcadRev.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditor_assignment", schema = "acadrev_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_profile_id", nullable = false)
    private CompanyProfile companyProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "external_auditor_id", nullable = false)
    private User externalAuditor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_by_internal_auditor_id", nullable = false)
    private User assignedByInternalAuditor;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
}

