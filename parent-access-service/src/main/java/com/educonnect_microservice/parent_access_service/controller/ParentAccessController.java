package com.educonnect_microservice.parent_access_service.controller;

import com.educonnect_microservice.parent_access_service.service.ParentAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/parent-access")
@RequiredArgsConstructor
public class ParentAccessController {
    private final ParentAccessService parentAccessService;
    @PostMapping("/link")
    public ResponseEntity<String> linkStudent(@RequestParam UUID parentId,@RequestParam UUID studentId){
        parentAccessService.linkParent(parentId, studentId);
        return ResponseEntity.ok("Student linked successfully");
    }

    @PostMapping("/grant")
    public ResponseEntity<String> grantAccess(@RequestParam UUID parentId,@RequestParam UUID studentId){
        parentAccessService.grantAccess(parentId, studentId);
        return ResponseEntity.ok("Access granted successfully");
    }

    @GetMapping("/get")
    public ResponseEntity<Map> getAccess(@RequestParam UUID parentId,@RequestParam UUID studentId){
        boolean hasAccess= parentAccessService.getAccess(parentId, studentId);
        Map<String,Object> response=new HashMap<>();
        response.put("parentId",parentId);
        response.put("studentId",studentId);
        response.put("hasAccess",hasAccess);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-verification")
    public ResponseEntity<Map> sendVerification(@RequestParam UUID parentId){
        String link= parentAccessService.sendVerification(parentId);
        Map<String,Object> response=new HashMap<>();
        response.put("message","Verification token generated");
        response.put("link",link);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token){
        parentAccessService.verifyParent(token);
        return ResponseEntity.ok("Parent verified successfully");
    }
}
