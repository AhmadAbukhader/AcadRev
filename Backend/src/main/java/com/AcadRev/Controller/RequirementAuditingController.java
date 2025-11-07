package com.AcadRev.Controller;

import com.AcadRev.Dto.RequirementAuditingDTO;
import com.AcadRev.Service.RequirementAuditingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirement-auditing")
@RequiredArgsConstructor
public class RequirementAuditingController {

    private final RequirementAuditingService auditingService;

    @PostMapping("/upsert")
    public ResponseEntity<RequirementAuditingDTO> upsertAudit(
            @RequestParam int requirementId,
            @RequestParam int companyId,
            @RequestParam int status
    ) {
        RequirementAuditingDTO dto = auditingService.upsertAudit(requirementId, companyId, status);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/progress/{companyId}")
    public ResponseEntity<Double> getProgress(@PathVariable int companyId) {
        double progress = auditingService.calculateProgress(companyId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/company/{companyId}/requirements")
    public ResponseEntity<List<RequirementAuditingDTO>> getRequirementsWithStatus(
            @PathVariable int companyId
    ) {
        List<RequirementAuditingDTO> result = auditingService.getRequirementsWithAuditStatus(companyId);
        return ResponseEntity.ok(result);
    }
}
