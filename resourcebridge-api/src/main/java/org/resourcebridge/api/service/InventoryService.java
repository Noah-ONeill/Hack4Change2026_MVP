package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Inventory;

import java.util.List;

public interface InventoryService extends GenericService<Inventory> {

    List<Inventory> findByOrganizationId(Long organizationId);

    // Items expiring within the next N days — used to trigger announcements
    List<Inventory> findExpiringSoon(int daysAhead);

    List<Inventory> findExpiringByOrganization(Long organizationId, int daysAhead);
}
