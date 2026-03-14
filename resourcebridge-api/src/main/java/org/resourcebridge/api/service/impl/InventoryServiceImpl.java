package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Inventory;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.InventoryRepository;
import org.resourcebridge.api.service.InventoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory getById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", id));
    }

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteById(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public List<Inventory> findByOrganizationId(Long organizationId) {
        return inventoryRepository.findByOrganizationId(organizationId);
    }

    @Override
    public List<Inventory> findExpiringSoon(int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return inventoryRepository.findByExpiryDateBefore(cutoff);
    }

    @Override
    public List<Inventory> findExpiringByOrganization(Long organizationId, int daysAhead) {
        LocalDate cutoff = LocalDate.now().plusDays(daysAhead);
        return inventoryRepository.findByOrganizationIdAndExpiryDateBefore(organizationId, cutoff);
    }
}
