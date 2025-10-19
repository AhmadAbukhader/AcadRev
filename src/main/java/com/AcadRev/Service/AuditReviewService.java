package com.AcadRev.Service;

import com.AcadRev.Model.AuditReview;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.User;
import com.AcadRev.Repository.AuditReviewRepository;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.DocumentRepository;
import com.AcadRev.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditReviewService {
    private final AuditReviewRepository auditReviewRepository;
    private final DocumentRepository documentRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyProfileService companyProfileService;

    public AuditReview reviewDocument(int documentId , int rating , String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User auditor = (User) auth.getPrincipal();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(()-> new RuntimeException("Document not found"));

        AuditReview auditReview = AuditReview.builder()
                .document(document)
                .auditor(auditor)
                .rating(rating)
                .comments(comment)
                .build();
        auditReviewRepository.save(auditReview);
        return auditReview;
    }

    public List<AuditReview> getAllCompanyReviews (int companyId) {
        return companyProfileService.getCompanyDocuments(companyId).stream()
                .map(d -> auditReviewRepository.findByDocumentId(d.getId()))
                .flatMap(Optional::stream)
                .toList();
    }

    public AuditReview getDocumentReview(int documentId) {
        return auditReviewRepository.findByDocumentId(documentId)
                .orElseThrow(()-> new RuntimeException("Document not reviewed"));
    }

}
