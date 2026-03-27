package com.ctx.course_service.utils.mapper;

public interface Mapper <E, Req,Res>{
    E toEntity(Req requestDTO);
    Res toResponseDTO(E entity);
}
