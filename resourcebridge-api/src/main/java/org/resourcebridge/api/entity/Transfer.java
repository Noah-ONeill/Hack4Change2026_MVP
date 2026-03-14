package org.resourcebridge.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.resourcebridge.api.enums.TransferStatus;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @ManyToOne
    @JoinColumn(name = "to_organization_id", nullable = false)
    private Organization toOrganization;

    @Column(nullable = false)
    private int quantityAssigned;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = TransferStatus.PENDING;
    }
}
