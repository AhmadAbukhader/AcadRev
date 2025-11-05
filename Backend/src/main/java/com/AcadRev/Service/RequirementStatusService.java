package com.AcadRev.Service;

import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.RequirementStatus;
import com.AcadRev.Model.User;
import com.AcadRev.Repository.RequirementRepository;
import com.AcadRev.Repository.RequirementStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequirementStatusService {
    private  RequirementStatusRepository requirementStatusRepository;
    private RequirementRepository requirementRepository;


    public List<RequirementStatus> getStatuses(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyOwner = (User) auth.getPrincipal();

        return requirementStatusRepository.findByUserId(companyOwner.getId());
    }

    public String updateStatus(int requirementId , int status){
        RequirementStatus requirementStatus = requirementStatusRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with ID: " + requirementId));
        requirementStatus.setStatus(status);
        requirementStatusRepository.save(requirementStatus);
        return "Status updated successfully";
    }
    public String createStatus(int requirementId , int status){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyOwner = (User) auth.getPrincipal();

        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with ID: " + requirementId));

        RequirementStatus requirementStatus = RequirementStatus.builder()
                .user(companyOwner)
                .status(status)
                .requirement(requirement)
                .build();

        requirementStatusRepository.save(requirementStatus);
        return "Status created successfully";
    }

    public int getStatusProgress(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User companyOwner = (User) auth.getPrincipal();

        List<RequirementStatus> requirementStatuses = requirementStatusRepository.findByUserId(companyOwner.getId());

        int totalIdSum = requirementStatuses.stream()
                .mapToInt(RequirementStatus::getStatus)
                .sum();
        int progress = (int) totalIdSum/124 ;
        return progress;
    }
}
