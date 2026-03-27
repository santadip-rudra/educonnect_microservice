package com.ctx.user_management_service.services;

import com.ctx.user_management_service.dto.parent.ParentResponse;
import com.ctx.user_management_service.dto.parent.ParentUpdateRequest;
import com.ctx.user_management_service.models.Parent;
import com.ctx.user_management_service.repo.ParentRepo;
import com.ctx.user_management_service.utils.ParentMapper;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentService {
    private final ParentRepo parentRepo;
    private final ParentMapper mapper;

    public ParentResponse updateParent(UUID parentId , ParentUpdateRequest request){
        Parent p =  parentRepo.findByParentId(parentId)
                .orElseGet(()-> new Parent(parentId));
        UpdateUtil.setIfPresent(request.getPhoneNumber(),p::setPhoneNumber);
        UpdateUtil.setIfPresent(request.getVerified(),p::setVerified);
        return mapper.toResponseDto(parentRepo.save(p));
    }

    public ParentResponse findParentById(UUID parentId) throws Exception {
        return parentRepo.findByParentId(parentId)
                .map(mapper::toResponseDto)
                .orElseThrow(()-> new Exception("Parent not found"));
    }

    public List<ParentResponse> findAll(){
        return  mapper.toResponseDtos(parentRepo.findAll());
    }

}
