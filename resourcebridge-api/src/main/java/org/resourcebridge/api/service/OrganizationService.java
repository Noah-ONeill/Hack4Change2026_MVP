package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationService extends GenericService<Organization> {

    Optional<Organization> findByName(String name);

    List<Organization> findByType(String type);
}
