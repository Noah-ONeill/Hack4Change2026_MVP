package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Donation;
import org.resourcebridge.api.enums.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    // Coordinator reviews all offered donations
    List<Donation> findByStatus(DonationStatus status);

    // Donor tracks their own donations by email
    List<Donation> findByDonorEmail(String donorEmail);

    // Find donations for a specific item — coordinator matching needs
    List<Donation> findByItemIdAndStatus(Long itemId, DonationStatus status);
}
