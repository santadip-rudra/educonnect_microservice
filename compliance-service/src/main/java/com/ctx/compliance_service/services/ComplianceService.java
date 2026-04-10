package com.ctx.compliance_service.services;

import com.ctx.compliance_service.client.UserClient;
import com.ctx.compliance_service.dto.ComplianceRecordRequestDTO;
import com.ctx.compliance_service.dto.ComplianceRecordResponseDTO;
import com.ctx.compliance_service.dto.UserResponse;
import com.ctx.compliance_service.exceptions.custom.ComplianceRecordNotFoundException;
import com.ctx.compliance_service.exceptions.custom.UserNotFoundException;
import com.ctx.compliance_service.models.ComplianceRecord;
import com.ctx.compliance_service.models.Note;
import com.ctx.compliance_service.repo.ComplianceRepo;
import com.ctx.compliance_service.repo.NoteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplianceService {

    private final ComplianceRepo recordRepository;
    private final NoteRepo noteRepository;

    @Transactional
    public ComplianceRecordResponseDTO createRecord(ComplianceRecordRequestDTO dto) throws UserNotFoundException {
        ComplianceRecord record = new ComplianceRecord();
        record.setUserId(dto.getUserId());
        record.setType(dto.getType());
        record.setResult(dto.getResult());

        LocalDate effectiveDate = (dto.getDate() != null)
                ? dto.getDate()
                : LocalDate.now(ZoneId.of("Asia/Kolkata"));
        record.setDate(effectiveDate);
        // Save record first
        ComplianceRecord savedRecord = recordRepository.save(record);

        // Add notes if present
        if (dto.getNotes() != null) {
            dto.getNotes().forEach(text -> {
                Note note = new Note();
                note.setNote(text);
                note.setComplianceRecord(savedRecord);
                noteRepository.save(note); // Persist note
                savedRecord.getNotes().add(note); // Sync memory
            });
        }

        return mapToDTO(savedRecord);
    }

    public ComplianceRecordResponseDTO getRecordById(UUID id) {
        ComplianceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ComplianceRecordNotFoundException("Record not found: " + id));
        // Just map the found entity to DTO and return it
        return mapToDTO(record);
    }


    public List<ComplianceRecordResponseDTO> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToDTO) // Direct mapping
                .collect(Collectors.toList());
    }


    @Transactional
    public ComplianceRecordResponseDTO updateRecord(UUID id, ComplianceRecordRequestDTO dto) {
        ComplianceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ComplianceRecordNotFoundException("Cannot update. Record not found: " + id));

        record.setType(dto.getType());
        record.setResult(dto.getResult());

        if (dto.getDate() != null) {
            record.setDate(dto.getDate());
        }

        // Handle Notes
        noteRepository.deleteAll(record.getNotes());
        record.getNotes().clear();

        if (dto.getNotes() != null) {
            dto.getNotes().forEach(text -> {
                Note note = new Note();
                note.setNote(text);
                note.setComplianceRecord(record);
                noteRepository.save(note);
                record.getNotes().add(note); // Keep memory in sync
            });
        }

        ComplianceRecord updated = recordRepository.save(record);
        return mapToDTO(updated); // Use the helper, NOT getRecordById()
    }
    // HELPER METHOD: Converts Entity to DTO safely
    private ComplianceRecordResponseDTO mapToDTO(ComplianceRecord record) {
        ComplianceRecordResponseDTO dto = new ComplianceRecordResponseDTO();
        dto.setComplianceRecordID(record.getComplianceRecordId());
        dto.setUserId(record.getUserId());
        dto.setType(record.getType());
        dto.setResult(record.getResult());
        dto.setDate(record.getDate());

        if (record.getNotes() != null) {
            dto.setNotes(record.getNotes().stream()
                    .map(Note::getNote)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}