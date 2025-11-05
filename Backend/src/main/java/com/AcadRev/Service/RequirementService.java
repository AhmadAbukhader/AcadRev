package com.AcadRev.Service;

import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Repository.DocumentRepository;
import com.AcadRev.Repository.RequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final DocumentRepository documentRepository;

    public List<Requirement> getAllRequirements() {
        return requirementRepository.findAll();
    }

    public List<Document> getDocumentsByRequirementId(int requirementId) {
        // Ensure requirement exists for a clearer 404 when empty
        requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with ID: " + requirementId));
        return documentRepository.findByRequirement_Id(requirementId);
    }

}
