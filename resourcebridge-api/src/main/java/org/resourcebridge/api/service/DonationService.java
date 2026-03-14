package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Donation;
import org.resourcebridge.api.enums.DonationStatus;

import java.util.List;

public interface DonationService extends GenericService<Donation> {

    List<Donation> findByStatus(DonationStatus status);

    List<Donation> findByDonorEmail(String donorEmail);

    // Find available (OFFERED) donations for a specific item — coordinator matching
    List<Donation> findAvailableByItem(Long itemId);

    Donation updateStatus(Long donationId, DonationStatus status);
}
