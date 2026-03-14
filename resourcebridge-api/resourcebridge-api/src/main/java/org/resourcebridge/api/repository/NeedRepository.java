package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Need;
import org.resourcebridge.api.enums.Urgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeedRepository extends JpaRepository<Need, Long> {

    List<Need> findByOrganizationId(Long organizationId);

    // All unfulfilled needs — main view for donors and coordinators
    List<Need> findByFulfilled(boolean fulfilled);

    // Unfulfilled needs for a specific org
    List<Need> findByOrganizationIdAndFulfilled(Long organizationId, boolean fulfilled);

    // Filter by urgency — coordinators prioritize CRITICAL needs
    List<Need> findByUrgencyAndFulfilled(Urgency urgency, boolean fulfilled);
}
