package com.AcadRev.Controller;

import com.AcadRev.Dto.UpdateReviewDto;
import com.AcadRev.Model.AuditReview;
import com.AcadRev.Service.AuditReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-review")
@RequiredArgsConstructor
public class AuditReviewController {
    private final AuditReviewService auditReviewService;

    @PostMapping("/document/{documentId}")
    @PreAuthorize("hasRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<AuditReview> reviewDocument(
            @PathVariable int documentId, @RequestParam String rating, @RequestParam String comment) {
        return ResponseEntity.ok(auditReviewService.reviewDocument(documentId, rating, comment));
    }

    @GetMapping("/documents/{companyId}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER', 'EXTERNAL_AUDITOR')")
    public ResponseEntity<List<AuditReview>> getAllCompanyDocumentsReview(@PathVariable int companyId) {
        return ResponseEntity.ok(auditReviewService.getAllCompanyReviews(companyId));
    }

    @GetMapping("/document/{documentId}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER', 'EXTERNAL_AUDITOR')")
    public ResponseEntity<List<AuditReview>> getDocumentReviews(@PathVariable int documentId) {
        return ResponseEntity.ok(auditReviewService.getDocumentReview(documentId));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<AuditReview> updateReview(@PathVariable int reviewId,
            @RequestBody UpdateReviewDto updateReviewDto) {
        AuditReview updatedReview = auditReviewService.updateReview(reviewId, updateReviewDto);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/auditor/{auditorId}")
    @PreAuthorize("hasRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<List<AuditReview>> getReviewsByAuditor(@PathVariable int auditorId) {
        List<AuditReview> reviews = auditReviewService.getReviewsByAuditor(auditorId);
        return ResponseEntity.ok(reviews);
    }

}
