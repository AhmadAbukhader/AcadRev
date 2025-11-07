package com.AcadRev.Repository;

import com.AcadRev.Model.RequirementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequirementResponseRepository extends JpaRepository<RequirementResponse, Integer> {
    Optional<RequirementResponse> findByCompanyProfileIdAndRequirementId(int companyId, int requirementId);
}
