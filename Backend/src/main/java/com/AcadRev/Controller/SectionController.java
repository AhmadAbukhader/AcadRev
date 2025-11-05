package com.AcadRev.Controller;

import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.Section;
import com.AcadRev.Service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @GetMapping
    public List<Section> getAllSections() {
        return sectionService.getAllSections();
    }

    @GetMapping("/{sectionId}/requirements")
    public List<Requirement> getRequirementsBySection(@PathVariable int sectionId) {
        return sectionService.getRequirementsBySectionId(sectionId);
    }
}
