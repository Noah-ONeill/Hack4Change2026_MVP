package org.resourcebridge.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.resourcebridge.api.enums.ItemCategory;

@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String unit; // cans, pieces, boxes, bottles, etc.

    private boolean expiryRelevant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;
}
