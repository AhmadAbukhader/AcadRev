package com.AcadRev.Service;

import com.AcadRev.Dto.RequirementResponseDTO;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.RequirementResponse;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.RequirementRepository;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.RequirementResponseRepository;
import com.AcadRev.Repository.UserRepository;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementResponseService {

    private final RequirementResponseRepository requirementResponseRepository;
    private final RequirementRepository requirementRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    // Helper method to get current authenticated user (reloaded from repository to
    // ensure relationships are loaded)
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        User principal = (User) auth.getPrincipal();
        // Reload from repository to ensure companyProfile is properly loaded
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Helper method to convert entity to DTO
    private RequirementResponseDTO toDTO(RequirementResponse response) {
        RequirementResponseDTO dto = new RequirementResponseDTO();
        dto.setId(response.getId());
        dto.setRequirementId(response.getRequirement().getId());
        dto.setCompanyId(response.getCompanyProfile().getId());
        dto.setResponseText(response.getResponseText());
        dto.setParentResponseId(response.getParentResponse() != null ? response.getParentResponse().getId() : null);
        dto.setCreatedByUserId(response.getCreatedBy() != null ? response.getCreatedBy().getId() : null);
        dto.setCreatedByUserName(response.getCreatedBy() != null ? response.getCreatedBy().getName() : null);
        dto.setCreatedByUserRole(response.getCreatedBy() != null && response.getCreatedBy().getRole() != null
                ? response.getCreatedBy().getRole().getRole().toString()
                : null);
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());
        return dto;
    }

    // Helper method to build response tree with replies
    private RequirementResponseDTO buildResponseTree(RequirementResponse response) {
        RequirementResponseDTO dto = toDTO(response);

        // Get all replies to this response
        List<RequirementResponse> replies = requirementResponseRepository
                .findByParentResponseIdOrderByCreatedAtAsc(response.getId());
        if (!replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(this::buildResponseTree)
                    .collect(Collectors.toList()));
        } else {
            dto.setReplies(new ArrayList<>());
        }

        return dto;
    }

    // 1️⃣ Create response (internal auditor only)
    public RequirementResponseDTO createResponse(int requirementId, int companyId, String responseText) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole().getRole() != UserType.INTERNAL_AUDITOR) {
            throw new UnauthorizedAccessException("Only internal auditors can create requirement responses");
        }

        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));

        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // Internal auditor must be assigned to this company
        boolean isAuthorized = currentUser.getCompanyProfile() != null
                && currentUser.getCompanyProfile().getId().equals(companyId);

        if (!isAuthorized) {
            throw new UnauthorizedAccessException("You can only create responses for your assigned company");
        }

        RequirementResponse response = new RequirementResponse();
        response.setRequirement(requirement);
        response.setCompanyProfile(company);
        response.setResponseText(responseText);
        response.setCreatedBy(currentUser);
        response.setParentResponse(null); // Top-level response

        RequirementResponse saved = requirementResponseRepository.save(response);
        return buildResponseTree(saved);
    }

    // 2️⃣ Create reply (external auditor or internal auditor only)
    public RequirementResponseDTO createReply(int parentResponseId, String responseText) {
        User currentUser = getCurrentUser();

        UserType role = currentUser.getRole().getRole();
        if (role != UserType.EXTERNAL_AUDITOR && role != UserType.INTERNAL_AUDITOR) {
            throw new UnauthorizedAccessException("Only auditors can reply to requirement responses");
        }

        RequirementResponse parentResponse = requirementResponseRepository.findById(parentResponseId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent response not found"));

        RequirementResponse reply = new RequirementResponse();
        reply.setRequirement(parentResponse.getRequirement());
        reply.setCompanyProfile(parentResponse.getCompanyProfile());
        reply.setResponseText(responseText);
        reply.setCreatedBy(currentUser);
        reply.setParentResponse(parentResponse);

        RequirementResponse saved = requirementResponseRepository.save(reply);
        return buildResponseTree(saved);
    }

    // 3️⃣ Update response (internal auditor only)
    public RequirementResponseDTO updateResponse(int id, String updatedText) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole().getRole() != UserType.INTERNAL_AUDITOR) {
            throw new UnauthorizedAccessException("Only internal auditors can update responses");
        }

        RequirementResponse response = requirementResponseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found"));

        // Check if user created this response
        if (!response.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You can only update your own responses");
        }

        response.setResponseText(updatedText);
        RequirementResponse updated = requirementResponseRepository.save(response);
        return buildResponseTree(updated);
    }

    // 4️⃣ Get all responses for a requirement (with replies) - returns top-level
    // responses with nested replies
    public List<RequirementResponseDTO> getAllResponses(int companyId, int requirementId) {
        List<RequirementResponse> topLevelResponses = requirementResponseRepository
                .findByCompanyProfileIdAndRequirementIdAndParentResponseIsNullOrderByCreatedAtAsc(companyId,
                        requirementId);

        return topLevelResponses.stream()
                .map(this::buildResponseTree)
                .collect(Collectors.toList());
    }

    // 5️⃣ Get response by company + requirement (for backward compatibility -
    // returns first top-level response)
    public RequirementResponseDTO getResponseByCompanyAndRequirement(int companyId, int requirementId) {
        List<RequirementResponseDTO> allResponses = getAllResponses(companyId, requirementId);

        if (allResponses.isEmpty()) {
            throw new ResourceNotFoundException("Response not found for this company and requirement");
        }

        // Return the first top-level response (for backward compatibility)
        return allResponses.get(0);
    }
}
