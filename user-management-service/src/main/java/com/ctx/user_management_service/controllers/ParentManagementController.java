package com.ctx.user_management_service.controllers;

import com.ctx.user_management_service.dto.parent.ParentResponse;
import com.ctx.user_management_service.dto.parent.ParentUpdateRequest;
import com.ctx.user_management_service.services.ParentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("parent")
@RequiredArgsConstructor
public class ParentManagementController
{
    private final ParentService parentService;

    @GetMapping
    public ResponseEntity<List<ParentResponse>> findAllParents(){
        return ResponseEntity.ok(parentService.findAll());
    }

    @GetMapping("{parentId}")
    public  ResponseEntity<ParentResponse> findParentById(@PathVariable UUID parentId) throws Exception {
        return  ResponseEntity.ok(parentService.findParentById(parentId));
    }

    @PostMapping
    public  ResponseEntity<ParentResponse> updateParent(@RequestBody ParentUpdateRequest request, @RequestHeader("X-User-Id") UUID parentId){
        return ResponseEntity.ok(parentService.updateParent(parentId,request));
    }
}
