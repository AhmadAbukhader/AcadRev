package com.AcadRev.Controller;

import com.AcadRev.Dto.CompanyProfileDto;
import com.AcadRev.Model.CompanyProfile;
import com.AcadRev.Model.Document;
import com.AcadRev.Service.CompanyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-profile")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyProfileService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('COMPANY_MANAGER')")
    public ResponseEntity<CompanyProfile> createCompanyProfile(@RequestBody CompanyProfileDto companyProfile) {
        return ResponseEntity.ok(companyProfileService.createProfile(companyProfile));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('EXTERNAL_AUDITOR', 'INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<List<CompanyProfile>> getAllCompaniesProfile() {
        return ResponseEntity.ok(companyProfileService.getCompanies());
    }

    @GetMapping("/list-for-signup")
    public ResponseEntity<List<CompanyProfile>> getCompaniesForSignup() {
        // Public endpoint for internal auditors to see available companies during signup
        return ResponseEntity.ok(companyProfileService.getCompanies());
    }

    @GetMapping("/my-company")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<CompanyProfile> getMyCompany() {
        // Gets the company for the current user
        // For managers: the company they created
        // For internal auditors: the company they're assigned to
        return ResponseEntity.ok(companyProfileService.getMyCompany());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EXTERNAL_AUDITOR')")
    public ResponseEntity<CompanyProfile> getCompanyProfile(@PathVariable int id) {
        return ResponseEntity.ok(companyProfileService.getCompany(id));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'EXTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<List<Document>> getCompanyDocuments(@PathVariable int id) {
        List<Document> documents = companyProfileService.getCompanyDocuments(id);
        return ResponseEntity.ok(documents);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_MANAGER')")
    public ResponseEntity<CompanyProfile> updateCompanyProfile(@PathVariable int id,
                                                               @RequestBody CompanyProfileDto companyProfileDto) {
        CompanyProfile updatedProfile = companyProfileService.updateProfile(id, companyProfileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_MANAGER')")
    public ResponseEntity<Void> deleteCompanyProfile(@PathVariable int id) {
        companyProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

}