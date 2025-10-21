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
    @PreAuthorize("hasAnyRole('COMPANY_OWNER')")
    public ResponseEntity<CompanyProfile> createCompanyProfile(@RequestBody CompanyProfileDto companyProfile) {
        return ResponseEntity.ok(companyProfileService.createProfile(companyProfile));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('AUDITOR')")
    public ResponseEntity<List<CompanyProfile>> getAllCompanyProfile() {
        return ResponseEntity.ok(companyProfileService.getCompanies());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR')")
    public ResponseEntity<CompanyProfile> getCompanyProfile(@PathVariable int id) {
        return ResponseEntity.ok(companyProfileService.getCompany(id));
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER', 'AUDITOR')")
    public ResponseEntity<List<Document>> getCompanyDocuments(@PathVariable int id) {
        List<Document> documents = companyProfileService.getCompanyDocuments(id);
        return ResponseEntity.ok(documents);
    }

    //test 

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER')")
    public ResponseEntity<CompanyProfile> updateCompanyProfile(@PathVariable int id,
            @RequestBody CompanyProfileDto companyProfileDto) {
        CompanyProfile updatedProfile = companyProfileService.updateProfile(id, companyProfileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPANY_OWNER')")
    public ResponseEntity<Void> deleteCompanyProfile(@PathVariable int id) {
        companyProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

}