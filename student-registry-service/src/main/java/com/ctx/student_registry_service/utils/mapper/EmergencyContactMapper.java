package com.ctx.student_registry_service.utils.mapper;


import com.ctx.student_registry_service.dto.demographics.EmergencyContactDto;
import com.ctx.student_registry_service.models.EmergencyContact;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmergencyContactMapper {
    EmergencyContactDto toDto(EmergencyContact entity);
    EmergencyContact toEntity(EmergencyContactDto dto);
}
