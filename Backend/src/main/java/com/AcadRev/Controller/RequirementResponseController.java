package com.AcadRev.Controller;

import com.AcadRev.Dto.RequirementResponseDTO;
import com.AcadRev.Service.RequirementResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requirement-responses")
@RequiredArgsConstructor
public class RequirementResponseController {

    private final RequirementResponseService requirementResponseService;

    @PostMapping
    public ResponseEntity<RequirementResponseDTO> createResponse(
            @RequestParam int requirementId,
            @RequestParam int companyId,
            @RequestParam String responseText) {

        RequirementResponseDTO response = requirementResponseService.createResponse(requirementId, companyId, responseText);
        return ResponseEntity.ok(response);
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
}
