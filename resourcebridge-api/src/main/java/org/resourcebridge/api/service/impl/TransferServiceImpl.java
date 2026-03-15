package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Inventory;
import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.enums.TransferStatus;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.InventoryRepository;
import org.resourcebridge.api.repository.TransferRepository;
import org.resourcebridge.api.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final InventoryRepository inventoryRepository;

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
    public void deleteAll() {
        transferRepository.deleteAll();
    }

    @Override
    public List<Transfer> findByOrganizationId(Long organizationId) {
        return transferRepository.findByToOrganizationId(organizationId);
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
    @Transactional
    public Transfer updateStatus(Long transferId, TransferStatus status) {
        Transfer transfer = getById(transferId);
        transfer.setStatus(status);
        Transfer saved = transferRepository.save(transfer);

        if (status == TransferStatus.RECEIVED) {
            addToInventory(transfer);
        }

        return saved;
    }

    private void addToInventory(Transfer transfer) {
        var org = transfer.getToOrganization();
        var item = transfer.getDonation().getItem();
        int qty = transfer.getQuantityAssigned();

        inventoryRepository.findByOrganizationAndItem(org, item)
                .ifPresentOrElse(
                        existing -> {
                            existing.setQuantity(existing.getQuantity() + qty);
                            inventoryRepository.save(existing);
                        },
                        () -> {
                            Inventory newEntry = new Inventory();
                            newEntry.setOrganization(org);
                            newEntry.setItem(item);
                            newEntry.setQuantity(qty);
                            newEntry.setExpiryDate(transfer.getDonation().getExpiryDate());
                            inventoryRepository.save(newEntry);
                        }
                );
    }
}
