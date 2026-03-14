package org.resourcebridge.api.repository;

import org.resourcebridge.api.entity.User;
import org.resourcebridge.api.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByOrganizationId(Long organizationId);

    boolean existsByEmail(String email);
}
