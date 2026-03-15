package org.resourcebridge.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.resourcebridge.api.enums.DonationStatus;
import org.resourcebridge.api.enums.DonationType;
import org.resourcebridge.api.entity.Organization;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stored as plain name+email+phone for donors (no account required)
    private String donorName;

    @Column(nullable = false)
    private String donorEmail;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status;

    // How the donation will be transferred
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationType donationType;

    // Donor's preferred shelter — system will try to match here first
    @ManyToOne
    @JoinColumn(name = "preferred_organization_id")
    private Organization preferredOrganization;

    // Only filled if donationType = PICKUP_REQUEST
    private String pickupAddress;
    private String pickupCity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = DonationStatus.OFFERED;
        if (donationType == null) donationType = DonationType.DROP_OFF;
    }
}
