package com.AcadRev.Service;

import com.AcadRev.Dto.DocumentMetadataDto;
import com.AcadRev.Dto.UpdateDocumentDto;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Model.Section;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.DocumentRepository;
import com.AcadRev.Repository.SectionRepository;
import com.AcadRev.Repository.RequirementRepository;
import com.AcadRev.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final SectionRepository sectionRepository;
    private final RequirementRepository requirementRepository;
    private final UserRepository userRepository;

    public Document uploadDocument(MultipartFile file, int companyId, String documentType, Integer sectionId,
            Integer requirementId) throws IOException {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Reload user from repository to ensure companyProfile is properly loaded
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        System.out.println("DEBUG: Upload attempt - User ID: " + user.getId() + ", Role: " + user.getRole().getRole() +
                ", CompanyProfile: " + (user.getCompanyProfile() != null ? user.getCompanyProfile().getId() : "null") +
                ", Target CompanyId: " + companyId);

        // Check authorization: either the user is the company owner (COMPANY_MANAGER)
        // or an INTERNAL_AUDITOR assigned to this company
        boolean isAuthorized = false;
        if (user.getRole().getRole() == UserType.COMPANY_MANAGER) {
            // Company manager must be the owner
            isAuthorized = company.getUser().getId().equals(user.getId());
            System.out.println("DEBUG: Company Manager check - Owner ID: " + company.getUser().getId() + ", User ID: "
                    + user.getId() + ", Authorized: " + isAuthorized);
        } else if (user.getRole().getRole() == UserType.INTERNAL_AUDITOR) {
            // Internal auditor must be assigned to this company
            isAuthorized = user.getCompanyProfile() != null
                    && user.getCompanyProfile().getId().equals(companyId);
            System.out.println("DEBUG: Internal Auditor check - User CompanyProfile: " +
                    (user.getCompanyProfile() != null ? user.getCompanyProfile().getId() : "null") +
                    ", Target CompanyId: " + companyId + ", Authorized: " + isAuthorized);
        }

        if (!isAuthorized) {
            throw new UnauthorizedAccessException("You are not authorized to upload documents for this company");
        }

        Section section = null;
        Requirement requirement = null;

        if (sectionId != null) {
            section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + sectionId));
        }
        if (requirementId != null) {
            requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Requirement not found with ID: " + requirementId));
        }
        // If both provided, ensure requirement belongs to section
        if (section != null && requirement != null && requirement.getSection() != null
                && requirement.getSection().getId() != section.getId()) {
            throw new IllegalArgumentException("Requirement does not belong to the provided section");
        }

        Document document = Document.builder()
                .company(company)
                .fileName(file.getOriginalFilename())
                .fileData(file.getBytes())
                .fileType(file.getContentType())
                .documentType(documentType)
                .section(section)
                .requirement(requirement)
                .build();

        return documentRepository.save(document);
    }

    public Document getDocumentById(int id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
    }

    public List<Document> getDocumentByName(String name) {
        List<Document> documents = documentRepository.findByFileName(name);
        if (documents.isEmpty()) {
            throw new ResourceNotFoundException("No documents found with name: " + name);
        }
        return documents;
    }

    public DocumentMetadataDto getDocumentMetadata(int id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        return DocumentMetadataDto.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .documentType(document.getDocumentType())
                .uploadedAt(document.getUploadedAt())
                .companyId(document.getCompany().getId())
                .companyName(document.getCompany().getName())
                .sectionId(document.getSection() != null ? document.getSection().getId() : null)
                .sectionName(document.getSection() != null ? document.getSection().getName() : null)
                .sectionCode(document.getSection() != null ? document.getSection().getCode() : null)
                .build();
    }

    public void deleteDocument(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        // Check if the current user is the owner of the company that owns this document
        if (!document.getCompany().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this document");
        }

        documentRepository.delete(document);
    }

    public DocumentMetadataDto updateDocument(int id, UpdateDocumentDto updateDocumentDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        // Check if the current user is the owner of the company that owns this document
        if (!document.getCompany().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this document");
        }

        // Update the document fields
        if (updateDocumentDto.getFileName() != null && !updateDocumentDto.getFileName().trim().isEmpty()) {
            document.setFileName(updateDocumentDto.getFileName());
        }
        if (updateDocumentDto.getDocumentType() != null && !updateDocumentDto.getDocumentType().trim().isEmpty()) {
            document.setDocumentType(updateDocumentDto.getDocumentType());
        }

        Document updatedDocument = documentRepository.save(document);

        return DocumentMetadataDto.builder()
                .id(updatedDocument.getId())
                .fileName(updatedDocument.getFileName())
                .fileType(updatedDocument.getFileType())
                .documentType(updatedDocument.getDocumentType())
                .uploadedAt(updatedDocument.getUploadedAt())
                .companyId(updatedDocument.getCompany().getId())
                .companyName(updatedDocument.getCompany().getName())
                .build();
    }

}
