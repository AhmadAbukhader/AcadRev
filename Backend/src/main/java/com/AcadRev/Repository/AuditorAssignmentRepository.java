package com.AcadRev.Repository;

import com.AcadRev.Model.AuditorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditorAssignmentRepository extends JpaRepository<AuditorAssignment, Integer> {

    // Find all companies assigned to a specific external auditor
    @Query("SELECT a FROM AuditorAssignment a WHERE a.externalAuditor.id = :auditorId AND a.isActive = true")
    List<AuditorAssignment> findByExternalAuditorIdAndIsActive(@Param("auditorId") Integer auditorId);

    // Find all external auditors assigned to a specific company
    @Query("SELECT a FROM AuditorAssignment a WHERE a.companyProfile.id = :companyId AND a.isActive = true")
    List<AuditorAssignment> findByCompanyProfileIdAndIsActive(@Param("companyId") Integer companyId);

    // Check if external auditor is assigned to a company
    @Query("SELECT a FROM AuditorAssignment a WHERE a.companyProfile.id = :companyId AND a.externalAuditor.id = :auditorId AND a.isActive = true")
    Optional<AuditorAssignment> findByCompanyProfileIdAndExternalAuditorIdAndIsActive(
            @Param("companyId") Integer companyId,
            @Param("auditorId") Integer auditorId
    );

    // Find all assignments for a specific internal auditor's company
    @Query("SELECT a FROM AuditorAssignment a WHERE a.assignedByInternalAuditor.id = :internalAuditorId AND a.isActive = true")
    List<AuditorAssignment> findByAssignedByInternalAuditorId(@Param("internalAuditorId") Integer internalAuditorId);
}

