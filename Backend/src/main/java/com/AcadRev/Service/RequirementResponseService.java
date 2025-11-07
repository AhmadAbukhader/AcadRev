package com.AcadRev.Service;

import com.AcadRev.Dto.RequirementResponseDTO;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.RequirementResponse;
import com.AcadRev.Repository.RequirementRepository;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.RequirementResponseRepository;
import com.AcadRev.Exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequirementResponseService {

    private final RequirementResponseRepository requirementResponseRepository;
    private final RequirementRepository requirementRepository;
    private final CompanyProfileRepository companyProfileRepository;

    // 1️⃣ Create response
    public RequirementResponseDTO createResponse(int requirementId, int companyId, String responseText) {
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));

        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        RequirementResponse response = new RequirementResponse();
        response.setRequirement(requirement);
        response.setCompanyProfile(company);
        response.setResponseText(responseText);

        RequirementResponse saved = requirementResponseRepository.save(response);

        // Build DTO directly
        RequirementResponseDTO dto = new RequirementResponseDTO();
        dto.setId(saved.getId());
        dto.setRequirementId(saved.getRequirement().getId());
        dto.setCompanyId(saved.getCompanyProfile().getId());
        dto.setResponseText(saved.getResponseText());

        return dto;
    }

    // 2️⃣ Update response
    public RequirementResponseDTO updateResponse(int id, String updatedText) {
        RequirementResponse response = requirementResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found"));

        response.setResponseText(updatedText);

        RequirementResponse updated = requirementResponseRepository.save(response);

        // Build DTO directly
        RequirementResponseDTO dto = new RequirementResponseDTO();
        dto.setId(updated.getId());
        dto.setRequirementId(updated.getRequirement().getId());
        dto.setCompanyId(updated.getCompanyProfile().getId());
        dto.setResponseText(updated.getResponseText());

        return dto;
    }

    // 3️⃣ Get response by company + requirement (for auditor)
    public RequirementResponseDTO getResponseByCompanyAndRequirement(int companyId, int requirementId) {
        RequirementResponse response = requirementResponseRepository
                .findByCompanyProfileIdAndRequirementId(companyId, requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found for this company and requirement"));

        // Build DTO directly
        RequirementResponseDTO dto = new RequirementResponseDTO();
        dto.setId(response.getId());
        dto.setRequirementId(response.getRequirement().getId());
        dto.setCompanyId(response.getCompanyProfile().getId());
        dto.setResponseText(response.getResponseText());

        return dto;
    }
}
