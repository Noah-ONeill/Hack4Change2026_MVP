package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.User;
import org.resourcebridge.api.enums.Role;

import java.util.List;

public interface UserService extends GenericService<User> {

    List<User> findByRole(Role role);

    List<User> findByOrganizationId(Long organizationId);
}
