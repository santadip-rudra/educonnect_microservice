package com.cts.auth_service.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "auth_users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt ;
//
//    @OneToMany(mappedBy = "user")
//    private List<Notification> notifications;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<AuditLog> auditLogs;

}
