package com.AcadRev.Repository;

import com.AcadRev.Model.RequirementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequirementResponseRepository extends JpaRepository<RequirementResponse, Integer> {
    Optional<RequirementResponse> findByCompanyProfileIdAndRequirementId(int companyId, int requirementId);
    
    // Find all responses for a requirement (including replies)
    List<RequirementResponse> findByCompanyProfileIdAndRequirementIdOrderByCreatedAtAsc(int companyId, int requirementId);
    
    // Find all top-level responses (no parent) for a requirement
    List<RequirementResponse> findByCompanyProfileIdAndRequirementIdAndParentResponseIsNullOrderByCreatedAtAsc(int companyId, int requirementId);
    
    // Find all replies to a specific response
    List<RequirementResponse> findByParentResponseIdOrderByCreatedAtAsc(int parentResponseId);
}
