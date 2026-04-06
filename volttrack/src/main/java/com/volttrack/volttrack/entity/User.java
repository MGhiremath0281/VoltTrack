package com.volttrack.volttrack.entity;

import com.volttrack.volttrack.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean active;

    @Column(unique = true, nullable = false)
    private String publicId;

    private Long approvedBy;   // ID of sub-district officer who approved
    private Boolean rejected;  // flag for rejection
    private Long suspendedBy;  // ID of sub-district officer who suspended

    // NEW: Relationship to Sub-District Officer
    @ManyToOne
    @JoinColumn(name = "sub_district_officer_id")
    private User subDistrictOfficer;

    @PrePersist
    public void generatePublicId() {
        if (this.publicId == null) {
            this.publicId = UUID.randomUUID().toString();
        }
    }
}
