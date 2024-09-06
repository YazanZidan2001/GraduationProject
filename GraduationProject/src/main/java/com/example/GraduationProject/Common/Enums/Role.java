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
                    Permission.DOCTOR_UPDATE
            )
    ),
    SECRETARY(
            Set.of(
                    Permission.SECRETARY_READ,
                    Permission.SECRETARY_UPDATE
            )
    ),
    WAREHOUSE_EMPLOYEE(
            Set.of(
                    Permission.DRUG_STORE_EMPLOYEE_READ,
                    Permission.DRUG_STORE_EMPLOYEE_UPDATE
            )
    ),PATIENT(
            Set.of(
                    Permission.PATIENT_READ,
                    Permission.PATIENT_UPDATE
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
