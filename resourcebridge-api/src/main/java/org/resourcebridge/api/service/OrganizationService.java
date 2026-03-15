package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.Organization;

import java.util.List;

public interface OrganizationService extends GenericService<Organization> {

    List<Organization> findByType(String type);
}
