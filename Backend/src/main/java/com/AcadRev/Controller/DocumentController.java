package com.AcadRev.Controller;

import com.AcadRev.Dto.DocumentMetadataDto;
import com.AcadRev.Dto.UpdateDocumentDto;
import com.AcadRev.Model.Document;
import com.AcadRev.Service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") int companyId,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "sectionId", required = false) Integer sectionId,
            @RequestParam(value = "requirementId", required = false) Integer requirementId) throws IOException {

        documentService.uploadDocument(file, companyId, documentType, sectionId, requirementId);
        return ResponseEntity.ok("File uploaded successfully!");
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'EXTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<byte[]> viewFile(@PathVariable int id) {
        Document document = documentService.getDocumentById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType() != null
                        ? document.getFileType()
                        : "application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + document.getFileName() + "\"")
                .body(document.getFileData());
    }

    @GetMapping("/metadata/{id}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR', 'EXTERNAL_AUDITOR', 'COMPANY_MANAGER')")
    public ResponseEntity<DocumentMetadataDto> getDocumentMetadata(@PathVariable int id) {
        DocumentMetadataDto metadata = documentService.getDocumentMetadata(id);
        return ResponseEntity.ok(metadata);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR')")
    public ResponseEntity<DocumentMetadataDto> updateDocument(@PathVariable int id,
            @RequestBody UpdateDocumentDto updateDocumentDto) {
        DocumentMetadataDto updatedDocument = documentService.updateDocument(id, updateDocumentDto);
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INTERNAL_AUDITOR')")
    public ResponseEntity<Void> deleteDocument(@PathVariable int id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

}
