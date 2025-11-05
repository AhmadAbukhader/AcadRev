package com.AcadRev.Repository;

import com.AcadRev.Model.RequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequirementStatusRepository extends JpaRepository<RequirementStatus, Integer> {

    List<RequirementStatus> findByUserId(int UserId);
}
