package org.resourcebridge.api.dto;

import lombok.Data;
import org.resourcebridge.api.enums.Role;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;           // COORDINATOR or STAFF
    private Long organizationId; // which org they belong to
}
