package com.ctx.compliance_service.services;

import com.ctx.compliance_service.dto.ComplianceRecordRequestDTO;
import com.ctx.compliance_service.dto.ComplianceRecordResponseDTO;
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

        // Add notes to the entity; cascade = ALL will persist them with the record.
        if (dto.getNotes() != null) {
            dto.getNotes().forEach(text -> {
                Note note = new Note();
                note.setNote(text);
                note.setComplianceRecord(record);
                record.getNotes().add(note);
            });
        }

        ComplianceRecord saved = recordRepository.save(record);
        return mapToDTO(saved);
    }

    public ComplianceRecordResponseDTO getRecordById(UUID id) {
        ComplianceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ComplianceRecordNotFoundException("Record not found: " + id));
        return mapToDTO(record);
    }

    public List<ComplianceRecordResponseDTO> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToDTO)
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

        // With orphanRemoval = true, clearing the list deletes the notes from the DB.
        record.getNotes().clear();

        if (dto.getNotes() != null) {
            dto.getNotes().forEach(text -> {
                Note note = new Note();
                note.setNote(text);
                note.setComplianceRecord(record);
                record.getNotes().add(note);
            });
        }

        ComplianceRecord updated = recordRepository.save(record);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteRecord(UUID id) {
        ComplianceRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ComplianceRecordNotFoundException("Cannot delete. Record not found: " + id));
        // cascade = ALL on notes removes them automatically
        recordRepository.delete(record);
    }

    // HELPER: Converts Entity to DTO safely
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