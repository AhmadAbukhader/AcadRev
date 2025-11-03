package com.AcadRev.Service;

import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.Section;
import com.AcadRev.Repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public List<Requirement> getRequirementsBySectionId(int sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + sectionId));
        return section.getRequirements();
    }
}
