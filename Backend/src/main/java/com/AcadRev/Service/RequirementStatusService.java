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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementStatusService {

    private final RequirementStatusRepository requirementStatusRepository;
    private final RequirementRepository requirementRepository;


    public List<RequirementStatus> getStatuses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        List<RequirementStatus> requirementStatusList =
                requirementStatusRepository.findByUserId(currentUser.getId());

        // If the user has no statuses yet, return default values in memory
        if (requirementStatusList.isEmpty()) {
            List<RequirementStatus> defaultStatuses = new ArrayList<>();

            // Example: assume you want one default status per requirement
            List<Requirement> allRequirements = requirementRepository.findAll();

            for (Requirement req : allRequirements) {
                RequirementStatus defaultStatus = RequirementStatus.builder()
                        .requirement(req)
                        .user(currentUser)
                        .status(0) // Default value, not saved to DB
                        .build();

                defaultStatuses.add(defaultStatus);
            }

            return defaultStatuses; // return in-memory defaults
        }

        return requirementStatusList;
    }
    public String upsertStatus(int requirementId, int status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // 1. Find the requirement
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Requirement not found with ID: " + requirementId));

        // 2. Find existing status for this user and requirement
        RequirementStatus requirementStatus =
                requirementStatusRepository.findByRequirementIdAndUserId(requirementId, currentUser.getId())
                        .orElseGet(() ->
                                // Create a new in-memory RequirementStatus if it doesn't exist
                                RequirementStatus.builder()
                                        .requirement(requirement)
                                        .user(currentUser)
                                        .build()
                        );

        // 3. Set the status value
        requirementStatus.setStatus(status);

        // 4. Save (insert or update)
        requirementStatusRepository.save(requirementStatus);

        return "Status saved successfully";
    }

    public int getStatusProgress() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // Fetch existing statuses for this user
        List<RequirementStatus> existingStatuses =
                requirementStatusRepository.findByUserId(currentUser.getId());

        // Fetch all requirements
        List<Requirement> allRequirements = requirementRepository.findAll();

        // Build a complete list: existing statuses + in-memory defaults for missing requirements
        Map<Integer, RequirementStatus> statusMap = existingStatuses.stream()
                .collect(Collectors.toMap(
                        rs -> rs.getRequirement().getId(),
                        rs -> rs
                ));

        List<RequirementStatus> completeStatuses = allRequirements.stream()
                .map(req -> statusMap.getOrDefault(
                        req.getId(),
                        RequirementStatus.builder()
                                .requirement(req)
                                .user(currentUser)
                                .status(0) // default
                                .build()
                ))
                .toList();

        int totalStatusSum = completeStatuses.stream()
                .mapToInt(RequirementStatus::getStatus)
                .sum();

        int numberOfRequirements = completeStatuses.size();
        int progress = numberOfRequirements > 0 ? (int) ((totalStatusSum * 100.0) / numberOfRequirements) : 0;

        return progress;
    }
}
