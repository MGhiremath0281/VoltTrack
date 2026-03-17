package com.volttrack.volttrack.entity;

import com.volttrack.volttrack.entity.enums.Billing;
import com.volttrack.volttrack.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", unique = true, nullable = false)
    private String publicId;

    @Column(unique = true, nullable = false)
    private String meterId;

    private String location;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Billing billing;

    @PrePersist
    public void generateIds() {

        if (this.publicId == null) {
            this.publicId = "MTR-" + UUID.randomUUID().toString().substring(0, 8);
        }

        if (this.meterId == null) {
            this.meterId = "ELEC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }
    }
}