package com.ctx.user_management_service.utils;

import com.ctx.user_management_service.dto.parent.ParentResponse;
import com.ctx.user_management_service.models.Parent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ParentMapper {

    public ParentResponse toResponseDto(Parent p){
        Objects.requireNonNull(p);
        ParentResponse response = new ParentResponse();
        UpdateUtil.setIfPresent(p.getParentId(),response::setParentId);
        UpdateUtil.setIfPresent(p.getPhoneNumber(),response::setPhoneNumber);
        UpdateUtil.setIfPresent(p.getVerified(),response::setVerified);
        return response;
    }

    public List<ParentResponse> toResponseDtos(List<Parent> parentList){
        return  parentList.stream().map(this::toResponseDto).toList();
    }
}