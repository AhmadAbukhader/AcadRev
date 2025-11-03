package com.AcadRev.Service;

import com.AcadRev.Dto.UpdateReviewDto;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;
import com.AcadRev.Model.AuditReview;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.User;
import com.AcadRev.Repository.AuditReviewRepository;
import com.AcadRev.Repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditReviewService {
    private final AuditReviewRepository auditReviewRepository;
    private final DocumentRepository documentRepository;
    private final CompanyProfileService companyProfileService;

    public AuditReview reviewDocument(int documentId, String rating, String comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User auditor = (User) auth.getPrincipal();

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        AuditReview auditReview = AuditReview.builder()
                .document(document)
                .auditor(auditor)
                .rating(rating)
                .comments(comment)
                .reviewedAt(LocalDateTime.now())
                .build();
        auditReviewRepository.save(auditReview);
        return auditReview;
    }

    public List<AuditReview> getAllCompanyReviews(int companyId) {
        return companyProfileService.getCompanyDocuments(companyId).stream()
                .map(d -> auditReviewRepository.findByDocumentId(d.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(List::stream)
                .toList();
    }

    public List<AuditReview> getDocumentReview(int documentId) {
        return auditReviewRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not reviewed"));
    }

    public AuditReview updateReview(int reviewId, UpdateReviewDto updateReviewDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        AuditReview existingReview = auditReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        // Check if the current user is the auditor who created this review
        if (!existingReview.getAuditor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this review");
        }

        // Update the review fields
        if (updateReviewDto.getRating() != null) {
            existingReview.setRating(updateReviewDto.getRating());
        }
        if (updateReviewDto.getComments() != null ) {
            existingReview.setComments(updateReviewDto.getComments());
        }

        return auditReviewRepository.save(existingReview);
    }

    public List<AuditReview> getReviewsByAuditor(int auditorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Check if the current user is requesting their own reviews or is an admin
        if (!currentUser.getId().equals(auditorId)) {
            throw new UnauthorizedAccessException("You are not authorized to view other auditors' reviews");
        }

        return auditReviewRepository.findByAuditorId(auditorId);
    }

}
