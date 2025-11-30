package com.AcadRev.Service;

import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.Requirement;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.DocumentRepository;
import com.AcadRev.Repository.RequirementRepository;
import com.AcadRev.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final DocumentRepository documentRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserRepository userRepository;

    public List<Requirement> getAllRequirements() {
        return requirementRepository.findAll();
    }

    public List<Document> getDocumentsByRequirementId(int requirementId) {
        // Ensure requirement exists for a clearer 404 when empty
        requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found with ID: " + requirementId));
        
        List<Document> allDocuments = documentRepository.findByRequirement_Id(requirementId);
        
        // Filter by current user's company
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            User currentUser = (User) auth.getPrincipal();
            // Reload user to ensure relationships are loaded
            currentUser = userRepository.findById(currentUser.getId())
                    .orElse(currentUser);
            
            Integer userCompanyId = getUserCompanyId(currentUser);
            
            if (userCompanyId != null) {
                System.out.println("DEBUG: Filtering documents for requirement " + requirementId + 
                                 " by company " + userCompanyId + 
                                 " (user: " + currentUser.getUsername() + ", role: " + currentUser.getRole().getRole() + ")");
                
                List<Document> filtered = allDocuments.stream()
                        .filter(doc -> doc.getCompany() != null && doc.getCompany().getId().equals(userCompanyId))
                        .collect(Collectors.toList());
                
                System.out.println("DEBUG: Found " + allDocuments.size() + " total documents, " + 
                                 filtered.size() + " for company " + userCompanyId);
                
                return filtered;
            }
        }
        
        // If no user context or no company, return all (for external auditors with access to multiple companies)
        return allDocuments;
    }
    
    private Integer getUserCompanyId(User user) {
        UserType role = user.getRole().getRole();
        
        if (role == UserType.INTERNAL_AUDITOR) {
            // Internal auditor: use assigned company
            return user.getCompanyProfile() != null ? user.getCompanyProfile().getId() : null;
        } else if (role == UserType.COMPANY_MANAGER) {
            // Manager: find company they own
            List<CompanyProfile> companies = companyProfileRepository.findAll();
            return companies.stream()
                    .filter(c -> c.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .map(CompanyProfile::getId)
                    .orElse(null);
        }
        // External auditors see all documents (they're assigned to multiple companies)
        return null;
    }

}
