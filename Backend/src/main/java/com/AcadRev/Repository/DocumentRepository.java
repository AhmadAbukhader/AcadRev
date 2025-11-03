package com.AcadRev.Repository;

import com.AcadRev.Model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByCompany_Id(int companyId);

    List<Document> findByFileName(String name);

    List<Document> findByRequirement_Id(int requirementId);
}
