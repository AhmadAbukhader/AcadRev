package com.AcadRev.Controller;

import com.AcadRev.Dto.RequirementResponseDTO;
import com.AcadRev.Service.RequirementResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirement-responses")
@RequiredArgsConstructor
public class RequirementResponseController {

    private final RequirementResponseService requirementResponseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<RequirementResponseDTO> createResponse(
            @RequestParam int requirementId,
            @RequestParam int companyId,
            @RequestParam String responseText) {

        RequirementResponseDTO response = requirementResponseService.createResponse(requirementId, companyId, responseText);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{parentResponseId}/reply")
    public ResponseEntity<RequirementResponseDTO> createReply(
            @PathVariable int parentResponseId,
            @RequestParam String responseText) {

        RequirementResponseDTO reply = requirementResponseService.createReply(parentResponseId, responseText);
        return ResponseEntity.ok(reply);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequirementResponseDTO> updateResponse(
            @PathVariable int id,
            @RequestParam String responseText) {

        RequirementResponseDTO updated = requirementResponseService.updateResponse(id, responseText);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{companyId}/{requirementId}")
    public ResponseEntity<RequirementResponseDTO> getResponse(
            @PathVariable int companyId,
            @PathVariable int requirementId) {

        RequirementResponseDTO response = requirementResponseService.getResponseByCompanyAndRequirement(companyId, requirementId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{companyId}/{requirementId}/all")
    public ResponseEntity<List<RequirementResponseDTO>> getAllResponses(
            @PathVariable int companyId,
            @PathVariable int requirementId) {

        List<RequirementResponseDTO> responses = requirementResponseService.getAllResponses(companyId, requirementId);
        return ResponseEntity.ok(responses);
    }
}
