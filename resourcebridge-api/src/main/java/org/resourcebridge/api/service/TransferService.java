package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.enums.TransferStatus;

import java.util.List;

public interface TransferService extends GenericService<Transfer> {

    List<Transfer> findByOrganizationId(Long organizationId);

    List<Transfer> findByStatus(TransferStatus status);

    List<Transfer> findByDonationId(Long donationId);

    Transfer updateStatus(Long transferId, TransferStatus status);

    void deleteAll();
}
