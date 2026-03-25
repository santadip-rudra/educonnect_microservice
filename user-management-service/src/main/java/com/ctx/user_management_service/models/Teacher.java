package com.ctx.user_management_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "teacher_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    private UUID teacherId;
    private String department;
    private String qualification;
}
