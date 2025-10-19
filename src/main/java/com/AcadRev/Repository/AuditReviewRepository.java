package com.AcadRev.Repository;

import com.AcadRev.Model.AuditReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuditReviewRepository extends JpaRepository<AuditReview, Integer> {
    Optional<AuditReview> findByDocumentId(int documentId);
}
