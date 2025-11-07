package com.AcadRev.Repository;

import com.AcadRev.Model.RequirementAuditing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequirementAuditingRepository extends JpaRepository<RequirementAuditing, Integer> {

    Optional<RequirementAuditing> findByRequirementIdAndCompanyProfileId(
            int requirementId, int companyId
    );
}
