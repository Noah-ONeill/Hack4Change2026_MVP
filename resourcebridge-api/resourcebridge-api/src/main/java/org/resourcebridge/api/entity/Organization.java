package org.resourcebridge.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type; // shelter, food_bank, community_center, etc.

    private String address;

    private String populationServed;

    private String contactEmail;

    private String contactPhone;
}
