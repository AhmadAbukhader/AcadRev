package com.AcadRev.Service;

import com.AcadRev.Dto.CompanyProfileDto;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.User;
import com.AcadRev.Model.UserType;
import com.AcadRev.Repository.CompanyProfileRepository;
import com.AcadRev.Repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyProfileService {

    private final CompanyProfileRepository companyProfileRepository;
    private final DocumentRepository documentRepository;

    public CompanyProfile createProfile(CompanyProfileDto companyProfile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        CompanyProfile createdProfile = CompanyProfile.builder()
                .user(currentUser)
                .phone(companyProfile.getPhone())
                .address(companyProfile.getAddress())
                .name(companyProfile.getName())
                .industry(companyProfile.getIndustry())
                .email(companyProfile.getEmail())
                .build();

        return companyProfileRepository.save(createdProfile);
    }

    public List<CompanyProfile> getCompanies() {
        return companyProfileRepository.findAll();
    }

    public CompanyProfile getCompany(int id) {
        return companyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
    }

    public CompanyProfile getMyCompany() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // For COMPANY_MANAGER: find the company they created (where they are the owner)
        if (currentUser.getRole().getRole() == UserType.COMPANY_MANAGER) {
            return companyProfileRepository.findAll().stream()
                    .filter(company -> company.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("You don't have a company profile yet"));
        }

        // For INTERNAL_AUDITOR: return the company they're assigned to
        if (currentUser.getRole().getRole() == UserType.INTERNAL_AUDITOR) {
            if (currentUser.getCompanyProfile() == null) {
                throw new ResourceNotFoundException("You are not assigned to any company");
            }
            return currentUser.getCompanyProfile();
        }

        throw new UnauthorizedAccessException("Only internal auditors and managers can access their company");
    }

    public List<Document> getCompanyDocuments(int companyId) {
        // Verify company exists
        companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        // Return all documents for this company
        return documentRepository.findByCompany_Id(companyId);
    }

    public CompanyProfile updateProfile(int id, CompanyProfileDto companyProfileDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        CompanyProfile existingProfile = companyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        // Check if the current user is the owner of this company profile
        if (!existingProfile.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to update this company profile");
        }

        // Update the profile fields
        existingProfile.setName(companyProfileDto.getName());
        existingProfile.setAddress(companyProfileDto.getAddress());
        existingProfile.setIndustry(companyProfileDto.getIndustry());
        existingProfile.setPhone(companyProfileDto.getPhone());
        existingProfile.setEmail(companyProfileDto.getEmail());

        System.out.println("DEBUG: Updating company profile ID: " + id);
        System.out.println("DEBUG: Email from DTO: " + companyProfileDto.getEmail());
        System.out.println("DEBUG: Email being set: " + existingProfile.getEmail());

        try {
            CompanyProfile saved = companyProfileRepository.save(existingProfile);
            System.out.println("DEBUG: Email after save: " + saved.getEmail());
            
            // Verify email was saved correctly
            if (companyProfileDto.getEmail() != null && !companyProfileDto.getEmail().trim().isEmpty()) {
                if (saved.getEmail() == null || !saved.getEmail().equals(companyProfileDto.getEmail())) {
                    System.err.println("WARNING: Email was not saved correctly. Expected: " + companyProfileDto.getEmail() + ", Got: " + saved.getEmail());
                    System.err.println("WARNING: This might indicate the 'email' column doesn't exist in the database. Please run the migration script.");
                }
            }
            
            return saved;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to save company profile: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update company profile: " + e.getMessage(), e);
        }
    }

    public void deleteProfile(int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        CompanyProfile existingProfile = companyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        // Check if the current user is the owner of this company profile
        if (!existingProfile.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this company profile");
        }

        companyProfileRepository.delete(existingProfile);
    }

}
