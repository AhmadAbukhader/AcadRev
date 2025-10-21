package com.AcadRev.Service;

import com.AcadRev.Dto.CompanyProfileDto;
import com.AcadRev.Exception.ResourceNotFoundException;
import com.AcadRev.Exception.UnauthorizedAccessException;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Model.User;
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
        User companyOwner = (User) auth.getPrincipal();

        CompanyProfile createdProfile = CompanyProfile.builder()
                .user(companyOwner)
                .phone(companyProfile.getPhone())
                .address(companyProfile.getAddress())
                .name(companyProfile.getName())
                .industry(companyProfile.getIndustry())
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

        return companyProfileRepository.save(existingProfile);
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
