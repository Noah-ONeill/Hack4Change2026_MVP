package org.resourcebridge.api.controller;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.enums.TransferStatus;
import org.resourcebridge.api.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    public List<Transfer> getAll() {
        return transferService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transfer> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.getById(id));
    }

    // GET /api/transfers/organization/1 — shelter staff sees incoming transfers
    @GetMapping("/organization/{organizationId}")
    public List<Transfer> getByOrganization(@PathVariable Long organizationId) {
        return transferService.findByOrganizationId(organizationId);
    }

    // GET /api/transfers/coordinator/2 — coordinator sees their assigned transfers
    @GetMapping("/coordinator/{coordinatorId}")
    public List<Transfer> getByCoordinator(@PathVariable Long coordinatorId) {
        return transferService.findByCoordinatorId(coordinatorId);
    }

    // GET /api/transfers/donation/5 — check all transfers for a donation
    @GetMapping("/donation/{donationId}")
    public List<Transfer> getByDonation(@PathVariable Long donationId) {
        return transferService.findByDonationId(donationId);
    }

    // GET /api/transfers/status/PENDING
    @GetMapping("/status/{status}")
    public List<Transfer> getByStatus(@PathVariable TransferStatus status) {
        return transferService.findByStatus(status);
    }

    // POST /api/transfers — coordinator assigns donation to a shelter
    @PostMapping
    public ResponseEntity<Transfer> create(@RequestBody Transfer transfer) {
        return ResponseEntity.ok(transferService.save(transfer));
    }

    // PATCH /api/transfers/1/status?status=DELIVERED — update transfer progress
    @PatchMapping("/{id}/status")
    public ResponseEntity<Transfer> updateStatus(
            @PathVariable Long id,
            @RequestParam TransferStatus status) {
        return ResponseEntity.ok(transferService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transferService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
