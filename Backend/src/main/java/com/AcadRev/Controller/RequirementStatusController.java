package com.AcadRev.Controller;

import com.AcadRev.Model.RequirementStatus;
import com.AcadRev.Service.RequirementStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/requirements-status")
@RequiredArgsConstructor
public class RequirementStatusController {

    private final RequirementStatusService requirementStatusService;

    @GetMapping()
    public ResponseEntity<List<RequirementStatus>> getAllUserRequirementStatus(){
        return ResponseEntity.ok(requirementStatusService.getStatuses());
    }

    @PutMapping("/{requirementId}")
    public ResponseEntity<String> updateRequirementStatus(@PathVariable int requirementId, @RequestParam int status){
        return ResponseEntity.ok(requirementStatusService.upsertStatus(requirementId , status));
    }

    @GetMapping("/progress")
    public ResponseEntity<Integer> getRequirementStatusProgress(){
        return ResponseEntity.ok(requirementStatusService.getStatusProgress());
    }

}
