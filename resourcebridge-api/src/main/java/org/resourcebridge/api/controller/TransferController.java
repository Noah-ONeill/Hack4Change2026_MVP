package org.resourcebridge.api.controller;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Transfer;
import org.resourcebridge.api.entity.User;
import org.resourcebridge.api.enums.TransferStatus;
import org.resourcebridge.api.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // GET /api/transfers/my — staff sees ONLY their own organization's incoming transfers
    @GetMapping("/my")
    public ResponseEntity<List<Transfer>> getMyTransfers(@AuthenticationPrincipal User user) {
        if (user.getOrganization() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(transferService.findByOrganizationId(user.getOrganization().getId()));
    }

    // GET /api/transfers/organization/1 — shelter staff sees their incoming transfers
    @GetMapping("/organization/{organizationId}")
    public List<Transfer> getByOrganization(@PathVariable Long organizationId) {
        return transferService.findByOrganizationId(organizationId);
    }

    // GET /api/transfers/donation/5 — check transfers for a specific donation
    @GetMapping("/donation/{donationId}")
    public List<Transfer> getByDonation(@PathVariable Long donationId) {
        return transferService.findByDonationId(donationId);
    }

    // GET /api/transfers/status/PENDING
    @GetMapping("/status/{status}")
    public List<Transfer> getByStatus(@PathVariable TransferStatus status) {
        return transferService.findByStatus(status);
    }

    // PATCH /api/transfers/1/status?status=DELIVERED — staff confirms receipt
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

    // DELETE /api/transfers — staff clears all transfers (e.g. reset for testing)
    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        transferService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
