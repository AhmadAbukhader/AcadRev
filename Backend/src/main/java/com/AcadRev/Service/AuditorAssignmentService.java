package com.AcadRev.Service;

import com.AcadRev.Dto.AuditorAssignmentDto;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;
import com.AcadRev.Model.AuditorAssignment;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.AuditorAssignmentRepository;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditorAssignmentService {

    private final AuditorAssignmentRepository auditorAssignmentRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    // Assign external auditor to a company (by internal auditor or manager)
    public AuditorAssignmentDto assignExternalAuditor(Integer companyId, Integer externalAuditorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Verify the company exists
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // Verify authorization based on role
        if (currentUser.getRole().getRole() == UserType.INTERNAL_AUDITOR) {
            // For internal auditors: check if they're assigned to this company
            if (currentUser.getCompanyProfile() == null || 
                !currentUser.getCompanyProfile().getId().equals(companyId)) {
                throw new UnauthorizedAccessException("You can only assign auditors to your assigned company");
            }
        } else if (currentUser.getRole().getRole() == UserType.COMPANY_MANAGER) {
            // For managers: check if they own this company
            if (!company.getUser().getId().equals(currentUser.getId())) {
                throw new UnauthorizedAccessException("You can only assign auditors to your own company");
            }
        } else {
            throw new UnauthorizedAccessException("Only internal auditors and managers can assign external auditors");
        }

        // Verify the external auditor exists and has the correct role
        User externalAuditor = userRepository.findById(externalAuditorId)
                .orElseThrow(() -> new ResourceNotFoundException("External auditor not found"));

        if (externalAuditor.getRole().getRole() != UserType.EXTERNAL_AUDITOR) {
            throw new IllegalArgumentException("The selected user is not an external auditor");
        }

        // Check if already assigned
        var existingAssignment = auditorAssignmentRepository
                .findByCompanyProfileIdAndExternalAuditorIdAndIsActive(companyId, externalAuditorId);

        if (existingAssignment.isPresent()) {
            // Return existing assignment
            return mapToDto(existingAssignment.get());
        }

        // Create new assignment
        AuditorAssignment assignment = AuditorAssignment.builder()
                .companyProfile(company)
                .externalAuditor(externalAuditor)
                .assignedByInternalAuditor(currentUser)
                .isActive(true)
                .build();

        AuditorAssignment saved = auditorAssignmentRepository.save(assignment);
        return mapToDto(saved);
    }

    // Get all companies assigned to an external auditor
    public List<AuditorAssignmentDto> getCompaniesForExternalAuditor(Integer externalAuditorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Verify the user is requesting their own assignments
        if (!currentUser.getId().equals(externalAuditorId)) {
            throw new UnauthorizedAccessException("You can only view your own assignments");
        }

        List<AuditorAssignment> assignments = auditorAssignmentRepository
                .findByExternalAuditorIdAndIsActive(externalAuditorId);

        return assignments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Get all external auditors assigned to a company
    public List<AuditorAssignmentDto> getExternalAuditorsForCompany(Integer companyId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Verify the company exists
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        // Check authorization based on role
        if (currentUser.getRole().getRole() == UserType.INTERNAL_AUDITOR) {
            // For internal auditors: check if they're assigned to this company
            if (currentUser.getCompanyProfile() == null || 
                !currentUser.getCompanyProfile().getId().equals(companyId)) {
                throw new UnauthorizedAccessException("You can only view auditors for your assigned company");
            }
        } else if (currentUser.getRole().getRole() == UserType.COMPANY_MANAGER) {
            // For managers: check if they own this company
            if (!company.getUser().getId().equals(currentUser.getId())) {
                throw new UnauthorizedAccessException("You can only view auditors for your own company");
            }
        } else {
            throw new UnauthorizedAccessException("Only internal auditors and managers can view auditor assignments");
        }

        List<AuditorAssignment> assignments = auditorAssignmentRepository
                .findByCompanyProfileIdAndIsActive(companyId);

        return assignments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Remove external auditor assignment
    public void removeExternalAuditor(Integer assignmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        AuditorAssignment assignment = auditorAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        // Verify authorization based on role
        if (currentUser.getRole().getRole() == UserType.INTERNAL_AUDITOR) {
            // For internal auditors: check if they're assigned to this company
            if (currentUser.getCompanyProfile() == null || 
                !currentUser.getCompanyProfile().getId().equals(assignment.getCompanyProfile().getId())) {
                throw new UnauthorizedAccessException("You can only remove auditors from your assigned company");
            }
        } else if (currentUser.getRole().getRole() == UserType.COMPANY_MANAGER) {
            // For managers: check if they own this company
            if (!assignment.getCompanyProfile().getUser().getId().equals(currentUser.getId())) {
                throw new UnauthorizedAccessException("You can only remove auditors from your own company");
            }
        } else {
            throw new UnauthorizedAccessException("Only internal auditors and managers can remove auditor assignments");
        }

        // Soft delete by setting isActive to false
        assignment.setIsActive(false);
        auditorAssignmentRepository.save(assignment);
    }

    // Check if external auditor has access to a company
    public boolean hasAccess(Integer externalAuditorId, Integer companyId) {
        return auditorAssignmentRepository
                .findByCompanyProfileIdAndExternalAuditorIdAndIsActive(companyId, externalAuditorId)
                .isPresent();
    }

    // Get all available external auditors (for internal auditor to choose from)
    public List<User> getAvailableExternalAuditors() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().getRole() == UserType.EXTERNAL_AUDITOR)
                .collect(Collectors.toList());
    }

    private AuditorAssignmentDto mapToDto(AuditorAssignment assignment) {
        return AuditorAssignmentDto.builder()
                .id(assignment.getId())
                .companyProfileId(assignment.getCompanyProfile().getId())
                .companyName(assignment.getCompanyProfile().getName())
                .externalAuditorId(assignment.getExternalAuditor().getId())
                .externalAuditorName(assignment.getExternalAuditor().getName())
                .externalAuditorUsername(assignment.getExternalAuditor().getUsername())
                .assignedByInternalAuditorId(assignment.getAssignedByInternalAuditor().getId())
                .assignedByInternalAuditorName(assignment.getAssignedByInternalAuditor().getName())
                .assignedAt(assignment.getAssignedAt())
                .isActive(assignment.getIsActive())
                .build();
    }
}




