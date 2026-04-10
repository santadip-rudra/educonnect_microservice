package com.ctx.compliance_service.controllers;

import com.ctx.compliance_service.dto.ComplianceRecordRequestDTO;
import com.ctx.compliance_service.dto.ComplianceRecordResponseDTO;
import com.ctx.compliance_service.exceptions.custom.UserNotFoundException;
import com.ctx.compliance_service.services.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/compliance-records")
public class ComplianceRecordController {
    private final ComplianceService complianceService;

    @GetMapping("test")
    public String test(){
        return "test";
    }
    // POST: Create a new record
    @PostMapping
    public ResponseEntity<ComplianceRecordResponseDTO> createRecord(@RequestBody ComplianceRecordRequestDTO dto) throws  UserNotFoundException {
        return new ResponseEntity<>(complianceService.createRecord(dto), HttpStatus.CREATED);
    }

    // PUT: Update an existing record
    @PutMapping("/{id}")
    public ResponseEntity<ComplianceRecordResponseDTO> updateRecord(
            @PathVariable UUID id,
            @RequestBody ComplianceRecordRequestDTO dto) {
        return ResponseEntity.ok(complianceService.updateRecord(id, dto));
    }

    // GET: Retrieve a specific record
    @GetMapping("/{id}")
    public ResponseEntity<ComplianceRecordResponseDTO> getRecord(@PathVariable UUID id) {
        return ResponseEntity.ok(complianceService.getRecordById(id));
    }

    // GET: Retrieve all records
    @GetMapping
    public ResponseEntity<List<ComplianceRecordResponseDTO>> getAllRecords() {
        return ResponseEntity.ok(complianceService.getAllRecords());
    }
}
