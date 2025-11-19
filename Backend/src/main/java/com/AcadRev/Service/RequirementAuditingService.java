package com.AcadRev.Service;

import com.AcadRev.Dto.RequirementAuditingDTO;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.User;
import com.AcadRev.Model.RequirementAuditing;
import com.AcadRev.Repository.RequirementRepository;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.UserRepository;
import com.AcadRev.Repository.RequirementAuditingRepository;
import com.AcadRev.Exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementAuditingService {

    private final RequirementAuditingRepository auditingRepository;
    private final RequirementRepository requirementRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    public RequirementAuditingDTO upsertAudit(int requirementId, int companyId, int status) {
        // Get authenticated external auditor
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User externalAuditor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("External auditor not found"));

        // Get requirement and company
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));

        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // Check if audit record exists for this requirement + company
        RequirementAuditing audit = auditingRepository
                .findByRequirementIdAndCompanyProfileId(requirementId, companyId)
                .orElse(new RequirementAuditing());

        audit.setRequirement(requirement);
        audit.setCompanyProfile(company);
        audit.setAuditor(externalAuditor);
        audit.setStatus(status);

        RequirementAuditing saved = auditingRepository.save(audit);

        // Build DTO directly
        RequirementAuditingDTO dto = new RequirementAuditingDTO();
        dto.setId(saved.getId());
        dto.setRequirementId(saved.getRequirement().getId());
        dto.setCompanyId(saved.getCompanyProfile().getId());
        dto.setAuditorId(saved.getAuditor().getId());
        dto.setStatus(saved.getStatus());

        return dto;
    }


    public double calculateProgress(int companyId) {
        List<Requirement> allRequirements = requirementRepository.findAll();
        if (allRequirements.isEmpty()) return 0.0;

        int totalRequirements = allRequirements.size();
        int maxTotal = totalRequirements * 2; // max status per requirement = 2
        int sumStatuses = 0;

        for (Requirement req : allRequirements) {
            RequirementAuditing audit = auditingRepository
                    .findByRequirementIdAndCompanyProfileId(req.getId(), companyId)
                    .orElse(null);

            sumStatuses += (audit != null ? audit.getStatus() : 0);
        }

        return (sumStatuses * 100.0) / maxTotal;
    }


    public List<RequirementAuditingDTO> getRequirementsWithAuditStatus(int companyId) {
        List<Requirement> allRequirements = requirementRepository.findAll();
        List<RequirementAuditingDTO> result = new ArrayList<>();

        for (Requirement req : allRequirements) {
            RequirementAuditing audit = auditingRepository
                    .findByRequirementIdAndCompanyProfileId(req.getId(), companyId)
                    .orElse(null);

            RequirementAuditingDTO dto = new RequirementAuditingDTO();
            dto.setRequirementId(req.getId());
            dto.setCompanyId(companyId);

            if (audit != null) {
                dto.setId(audit.getId());
                dto.setAuditorId(audit.getAuditor().getId());
                dto.setStatus(audit.getStatus());
            } else {
                dto.setStatus(0);
            }

            result.add(dto);
        }

        return result;
    }
}
