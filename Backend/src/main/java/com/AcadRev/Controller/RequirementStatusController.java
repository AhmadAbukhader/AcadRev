package com.AcadRev.Controller;

import com.AcadRev.Model.RequirementStatus;
import com.AcadRev.Service.RequirementStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirements-status")
@RequiredArgsConstructor
public class RequirementStatusController {

    private final RequirementStatusService requirementStatusService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<List<RequirementStatus>> getAllUserRequirementStatus() {
        return ResponseEntity.ok(requirementStatusService.getStatuses());
    }

    @PutMapping("/{requirementId}")
    @PreAuthorize("hasRole('INTERNAL_AUDITOR')")
    public ResponseEntity<String> updateRequirementStatus(@PathVariable int requirementId, @RequestParam int status) {
        return ResponseEntity.ok(requirementStatusService.upsertStatus(requirementId, status));
    }

    @GetMapping("/progress")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<Integer> getRequirementStatusProgress() {
        return ResponseEntity.ok(requirementStatusService.getStatusProgress());
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<List<RequirementStatus>> getCompanyRequirementStatuses(@PathVariable int companyId) {
        return ResponseEntity.ok(requirementStatusService.getStatusesByCompanyId(companyId));
    }

}
