package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Announcement;
import org.resourcebridge.api.enums.AnnouncementType;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.AnnouncementRepository;
import org.resourcebridge.api.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> getAll() {
        return announcementRepository.findAll();
    }

    @Override
    public Announcement getById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));
    }

    @Override
    public Announcement save(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    public void deleteById(Long id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public List<Announcement> findByOrganizationId(Long organizationId) {
        return announcementRepository.findByOrganizationId(organizationId);
    }

    @Override
    public List<Announcement> findByType(AnnouncementType type) {
        return announcementRepository.findByType(type);
    }

    @Override
    public List<Announcement> findLatest() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }
}
