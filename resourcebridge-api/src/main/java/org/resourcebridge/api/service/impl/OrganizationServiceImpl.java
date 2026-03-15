package org.resourcebridge.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.resourcebridge.api.entity.Organization;
import org.resourcebridge.api.exception.ResourceNotFoundException;
import org.resourcebridge.api.repository.OrganizationRepository;
import org.resourcebridge.api.service.OrganizationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

    @Override
    public Organization getById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
    }

    @Override
    public Organization save(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public void deleteById(Long id) {
        organizationRepository.deleteById(id);
    }

    @Override
    public List<Organization> findByType(String type) {
        return organizationRepository.findByType(type);
    }
}
