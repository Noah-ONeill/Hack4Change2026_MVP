package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Inventory;
import org.resourcebridge.api.entity.Item;
import org.resourcebridge.api.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByOrganization(Organization organization);

    List<Inventory> findByOrganizationId(Long organizationId);

    Optional<Inventory> findByOrganizationAndItem(Organization organization, Item item);

    // Find expiring items across all orgs — used for announcements/waste reduction
    List<Inventory> findByExpiryDateBefore(LocalDate date);

    // Find expiring items for a specific org
    List<Inventory> findByOrganizationIdAndExpiryDateBefore(Long organizationId, LocalDate date);
}
