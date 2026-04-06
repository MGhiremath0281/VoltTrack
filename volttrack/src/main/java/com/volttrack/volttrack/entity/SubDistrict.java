package com.volttrack.volttrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sub_districts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubDistrict {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;
}
