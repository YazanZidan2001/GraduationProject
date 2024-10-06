package com.example.GraduationProject.Common.Enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN(
            Set.of(
                    Permission.ADMIN_READ,
                    Permission.ADMIN_UPDATE,
                    Permission.ADMIN_DELETE,
                    Permission.ADMIN_CREATE
            )
    ),
    DOCTOR(
            Set.of(
                    Permission.DOCTOR_READ,
                    Permission.DOCTOR_UPDATE,
                    Permission.DOCTOR_DELETE,
                    Permission.DOCTOR_CREATE
            )
    ),
    SUPER_ADMIN(
            Set.of(
                    Permission.SUPER_ADMIN_READ,
                    Permission.SUPER_ADMIN_UPDATE,
                    Permission.SUPER_ADMIN_DELETE,
                    Permission.SUPER_ADMIN_CREATE
            )
    ),
    PATIENT(
            Set.of(
                    Permission.PATIENT_READ,
                    Permission.PATIENT_UPDATE,
                    Permission.PATIENT_DELETE,
                    Permission.PATIENT_CREATE
            ));

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
