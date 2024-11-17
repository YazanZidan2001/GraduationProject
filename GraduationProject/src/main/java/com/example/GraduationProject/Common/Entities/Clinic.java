package com.example.GraduationProject.Common.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clinic")
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private long clinicId;

    @Column(name = "clinic_name", nullable = false)
    @NotNull(message = "Clinic name cannot be blank")
    private String clinicName;

    @Column(name = "address", nullable = false)
    @NotNull(message = "Address cannot be blank")
    private String address;

    @Column(name = "street", nullable = false)
    @NotNull(message = "Street cannot be blank")
    private String street;

    @Column(name = "phone", nullable = false)
    @NotNull(message = "Phone cannot be blank")
    private String phone;
}
