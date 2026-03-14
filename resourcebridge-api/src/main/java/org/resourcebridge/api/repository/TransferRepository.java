package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.enums.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByToOrganizationId(Long organizationId);

    List<Transfer> findByStatus(TransferStatus status);

    List<Transfer> findByDonationId(Long donationId);
}
