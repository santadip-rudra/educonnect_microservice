package com.ctx.student_registry_service.utils.mapper;


import com.ctx.student_registry_service.dto.demographics.StudentDemographicsDTO;
import com.ctx.student_registry_service.models.StudentDemographics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DemographicsMapper {
    DemographicsMapper INSTANCE = Mappers.getMapper(DemographicsMapper.class);
    StudentDemographicsDTO toDto(StudentDemographics entity);

    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "bloodGroup",source = "bloodGroup")
    @Mapping(target = "visaType",source = "visaType")
    StudentDemographics toEntity(StudentDemographicsDTO dto);
}
