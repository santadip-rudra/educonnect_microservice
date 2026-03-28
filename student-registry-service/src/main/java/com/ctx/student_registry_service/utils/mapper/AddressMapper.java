package com.ctx.student_registry_service.utils.mapper;

import com.ctx.student_registry_service.dto.demographics.AddressDto;
import com.ctx.student_registry_service.models.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDto toDto(Address address);
    Address toEntity(AddressDto dto);
}
