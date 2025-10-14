package com.AcadRev.Service;

import com.AcadRev.Dto.CompanyProfileDto;
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
import java.util.Optional;

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

        companyProfileRepository.save(createdProfile);
        return createdProfile;
    }

    public List<CompanyProfile> getCompanies() {
        return companyProfileRepository.findAll();
    }

    public CompanyProfile getCompany(int id) {
        Optional<CompanyProfile> profileOpt = companyProfileRepository.findById(id);
        if (profileOpt == null) {
            throw new RuntimeException("no such company");
        }
        return profileOpt.get();
    }

    public List<Document> getCompanyDocuments(int companyId) {
        // Verify company exists
        companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found with ID: " + companyId));

        // Return all documents for this company
        return documentRepository.findByCompany_Id(companyId);
    }

}
