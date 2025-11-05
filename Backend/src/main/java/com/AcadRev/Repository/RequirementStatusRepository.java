package com.AcadRev.Repository;

import com.AcadRev.Model.RequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequirementStatusRepository extends JpaRepository<RequirementStatus, Integer> {

    Optional<RequirementStatus> findByRequirementIdAndUserId(int RequirementI , int UserId);
    List<RequirementStatus> findByUserId(int UserId);
}
