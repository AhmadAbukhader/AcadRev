package com.AcadRev.Controller;

import com.AcadRev.Model.Document;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'AUDITOR')")
    public List<Requirement> getAllRequirements() {
        return requirementService.getAllRequirements();
    }

    @GetMapping("/{requirementId}/documents")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'AUDITOR')")
    public List<Document> getDocumentsByRequirement(@PathVariable int requirementId) {
        return requirementService.getDocumentsByRequirementId(requirementId);
    }
}
