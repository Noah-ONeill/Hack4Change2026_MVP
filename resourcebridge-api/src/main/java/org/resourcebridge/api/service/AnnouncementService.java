package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Announcement;
import org.resourcebridge.api.enums.AnnouncementType;

import java.util.List;

public interface AnnouncementService extends GenericService<Announcement> {

    List<Announcement> findByOrganizationId(Long organizationId);

    List<Announcement> findByType(AnnouncementType type);

    List<Announcement> findLatest();
}
