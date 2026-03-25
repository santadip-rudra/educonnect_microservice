package com.ctx.user_management_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "parent_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Parent {
    @Id
    private UUID parentId;
    private String phoneNumber;
    private Boolean verified=false;
}
