package org.resourcebridge.api.service;

import org.resourcebridge.api.entity.User;
import org.resourcebridge.api.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService extends GenericService<User> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByOrganizationId(Long organizationId);

    boolean existsByEmail(String email);
}
