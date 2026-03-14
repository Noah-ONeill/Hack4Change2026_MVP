package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.enums.TransferStatus;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.TransferRepository;
import org.resourcebridge.api.service.TransferService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;

    @Override
    public List<Transfer> getAll() {
        return transferRepository.findAll();
    }

    @Override
    public Transfer getById(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", id));
    }

    @Override
    public Transfer save(Transfer transfer) {
        return transferRepository.save(transfer);
    }

    @Override
    public void deleteById(Long id) {
        transferRepository.deleteById(id);
    }

    @Override
    public List<Transfer> findByOrganizationId(Long organizationId) {
        return transferRepository.findByToOrganizationId(organizationId);
    }

    @Override
    public List<Transfer> findByCoordinatorId(Long coordinatorId) {
        return transferRepository.findByCoordinatorId(coordinatorId);
    }

    @Override
    public List<Transfer> findByStatus(TransferStatus status) {
        return transferRepository.findByStatus(status);
    }

    @Override
    public List<Transfer> findByDonationId(Long donationId) {
        return transferRepository.findByDonationId(donationId);
    }

    @Override
    public Transfer updateStatus(Long transferId, TransferStatus status) {
        Transfer transfer = getById(transferId);
        transfer.setStatus(status);
        return transferRepository.save(transfer);
    }
}
