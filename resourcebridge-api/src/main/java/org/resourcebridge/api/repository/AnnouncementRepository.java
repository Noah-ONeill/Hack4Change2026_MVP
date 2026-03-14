package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.Announcement;
import org.resourcebridge.api.enums.AnnouncementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByOrganizationId(Long organizationId);

    // Get all expiry/surplus/urgent announcements — coordinator dashboard
    List<Announcement> findByType(AnnouncementType type);

    // Most recent announcements first
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
