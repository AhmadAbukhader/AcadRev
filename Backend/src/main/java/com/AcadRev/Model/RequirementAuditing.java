package com.AcadRev.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RequirementAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile companyProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id", nullable = false)
    private User auditor;  // auditor is a type of User

    @Column(nullable = false)
    private int status;

}
