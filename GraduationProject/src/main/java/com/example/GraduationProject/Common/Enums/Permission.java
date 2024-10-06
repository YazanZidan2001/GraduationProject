package com.example.GraduationProject.Common.Enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    //ADMIN
    ADMIN_READ("Admin:read"),
    ADMIN_UPDATE("Admin:update"),
    ADMIN_CREATE("Admin:create"),
    ADMIN_DELETE("Admin:delete"),
    //DOCTOR
    DOCTOR_READ("Doctor:read"),
    DOCTOR_UPDATE("Doctor:update"),
    DOCTOR_CREATE("Doctor:create"),
    DOCTOR_DELETE("Doctor:delete"),
    //PATIENT
    PATIENT_READ("Patient:read"),
    PATIENT_UPDATE("Patient:update"),
    PATIENT_CREATE("Patient:create"),
    PATIENT_DELETE("Patient:delete"),

    SUPER_ADMIN_READ("SUPER_ADMIN:read"),
    SUPER_ADMIN_UPDATE("SUPER_ADMIN:update"),
    SUPER_ADMIN_CREATE("SUPER_ADMIN:create"),
    SUPER_ADMIN_DELETE("SUPER_ADMIN:delete");

    private final String permission;
}
