package com.AcadRev.Controller;

import com.AcadRev.Dto.AuditorAssignmentDto;
import com.AcadRev.Model.User;
import com.AcadRev.Service.AuditorAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auditor-assignments")
@RequiredArgsConstructor
public class AuditorAssignmentController {

    private final AuditorAssignmentService auditorAssignmentService;

    // Assign external auditor to company (internal auditor and manager)
    @PostMapping
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<AuditorAssignmentDto> assignExternalAuditor(
            @RequestBody Map<String, Integer> request) {
        Integer companyId = request.get("companyId");
        Integer externalAuditorId = request.get("externalAuditorId");
        
        AuditorAssignmentDto assignment = auditorAssignmentService.assignExternalAuditor(
                companyId, externalAuditorId);
        return ResponseEntity.ok(assignment);
    }

    // Get all companies for an external auditor
    @GetMapping("/external-auditor/{auditorId}/companies")
    @PreAuthorize("hasAnyRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<List<AuditorAssignmentDto>> getCompaniesForExternalAuditor(
            @PathVariable Integer auditorId) {
        List<AuditorAssignmentDto> assignments = auditorAssignmentService
                .getCompaniesForExternalAuditor(auditorId);
        return ResponseEntity.ok(assignments);
    }

    // Get all external auditors for a company
    @GetMapping("/company/{companyId}/external-auditors")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<List<AuditorAssignmentDto>> getExternalAuditorsForCompany(
            @PathVariable Integer companyId) {
        List<AuditorAssignmentDto> assignments = auditorAssignmentService
                .getExternalAuditorsForCompany(companyId);
        return ResponseEntity.ok(assignments);
    }

    // Get all available external auditors (for selection)
    @GetMapping("/available-external-auditors")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<List<User>> getAvailableExternalAuditors() {
        List<User> auditors = auditorAssignmentService.getAvailableExternalAuditors();
        return ResponseEntity.ok(auditors);
    }

    // Remove external auditor assignment
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<Void> removeExternalAuditor(@PathVariable Integer assignmentId) {
        auditorAssignmentService.removeExternalAuditor(assignmentId);
        return ResponseEntity.noContent().build();
    }

    // Check if external auditor has access to a company
    @GetMapping("/check-access")
    @PreAuthorize("hasAnyRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<Map<String, Boolean>> checkAccess(
            @RequestParam Integer externalAuditorId,
            @RequestParam Integer companyId) {
        boolean hasAccess = auditorAssignmentService.hasAccess(externalAuditorId, companyId);
        return ResponseEntity.ok(Map.of("hasAccess", hasAccess));
    }
}

