package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // All transfers assigned to a specific shelter
    List<Transfer> findByToOrganizationId(Long organizationId);

    // All transfers made by a specific coordinator
    List<Transfer> findByCoordinatorId(Long coordinatorId);

    // Track transfers by status (PENDING, DELIVERED, RECEIVED)
    List<Transfer> findByStatus(TransferStatus status);

    // All transfers for a specific donation — check if donation is fully assigned
    List<Transfer> findByDonationId(Long donationId);
}
