package com.AcadRev.Service;

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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final CompanyProfileRepository companyProfileRepository;


    public Document uploadDocument(MultipartFile file, int companyId, String documentType) throws IOException {
        CompanyProfile company = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyOwner = (User) auth.getPrincipal();

        if (company.getUser().getId() != companyOwner.getId()) {
            new RuntimeException("You are not the owner of this company");
        }

        Document document = Document.builder()
                .company(company)
                .fileName(file.getOriginalFilename())
                .fileData(file.getBytes())
                .fileType(file.getContentType())
                .documentType(documentType)
                .uploadedAt(LocalDateTime.now())
                .build();

        documentRepository.save(document);
        return document;
    }

    public Document getDocumentById(int id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + id));
    }

    public List<Document> getDocumentByName(String name) {
        List<Document> documents = documentRepository.findByFileName(name);
        if (documents.isEmpty()) {
            throw new RuntimeException("No documents found with name: " + name);
        }
        return documents;
    }


}
