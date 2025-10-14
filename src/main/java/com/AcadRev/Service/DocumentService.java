package com.AcadRev.Service;

import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.User;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.DocumentRepository;
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

    public Document uploadDocument(MultipartFile file, int companyId, String documentType) throws IOException {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyOwner = (User) auth.getPrincipal();

        if (!company.getUser().getId().equals(companyOwner.getId())) {
            throw new UnauthorizedAccessException("You are not the owner of this company");
        }

        Document document = Document.builder()
                .company(company)
                .fileName(file.getOriginalFilename())
                .fileData(file.getBytes())
                .fileType(file.getContentType())
                .documentType(documentType)
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

}
