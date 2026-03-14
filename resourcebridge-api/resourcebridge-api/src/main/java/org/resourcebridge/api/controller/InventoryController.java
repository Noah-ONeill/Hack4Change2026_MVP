package org.resourcebridge.api.controller;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Inventory;
import org.resourcebridge.api.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<Inventory> getAll() {
        return inventoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getById(id));
    }

    @GetMapping("/organization/{organizationId}")
    public List<Inventory> getByOrganization(@PathVariable Long organizationId) {
        return inventoryService.findByOrganizationId(organizationId);
    }

    // GET /api/inventory/expiring?days=7
    @GetMapping("/expiring")
    public List<Inventory> getExpiringSoon(@RequestParam(defaultValue = "7") int days) {
        return inventoryService.findExpiringSoon(days);
    }

    // GET /api/inventory/expiring/organization/1?days=7
    @GetMapping("/expiring/organization/{organizationId}")
    public List<Inventory> getExpiringByOrganization(
            @PathVariable Long organizationId,
            @RequestParam(defaultValue = "7") int days) {
        return inventoryService.findExpiringByOrganization(organizationId, days);
    }

    @PostMapping
    public ResponseEntity<Inventory> create(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.save(inventory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @RequestBody Inventory updated) {
        Inventory existing = inventoryService.getById(id);
        existing.setQuantity(updated.getQuantity());
        existing.setExpiryDate(updated.getExpiryDate());
        return ResponseEntity.ok(inventoryService.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
