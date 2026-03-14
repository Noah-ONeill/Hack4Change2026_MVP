package org.resourcebridge.api.controller;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Donation;
import org.resourcebridge.api.enums.DonationStatus;
import org.resourcebridge.api.service.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    // GET /api/donations — coordinator sees all OFFERED donations
    @GetMapping
    public List<Donation> getOffered() {
        return donationService.findByStatus(DonationStatus.OFFERED);
    }

    @GetMapping("/all")
    public List<Donation> getAll() {
        return donationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getById(id));
    }

    // GET /api/donations/status/ASSIGNED
    @GetMapping("/status/{status}")
    public List<Donation> getByStatus(@PathVariable DonationStatus status) {
        return donationService.findByStatus(status);
    }

    // GET /api/donations/donor?email=john@email.com — donor tracks their donations
    @GetMapping("/donor")
    public List<Donation> getByDonorEmail(@RequestParam String email) {
        return donationService.findByDonorEmail(email);
    }

    // GET /api/donations/available/item/3 — find available donations for a specific item
    @GetMapping("/available/item/{itemId}")
    public List<Donation> getAvailableByItem(@PathVariable Long itemId) {
        return donationService.findAvailableByItem(itemId);
    }

    // POST /api/donations — donor submits a donation offer (no token required)
    @PostMapping
    public ResponseEntity<Donation> create(@RequestBody Donation donation) {
        return ResponseEntity.ok(donationService.save(donation));
    }

    // PATCH /api/donations/1/status?status=ASSIGNED — coordinator updates status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Donation> updateStatus(
            @PathVariable Long id,
            @RequestParam DonationStatus status) {
        return ResponseEntity.ok(donationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
