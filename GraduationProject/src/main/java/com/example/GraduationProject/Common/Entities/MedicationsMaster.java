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
@Table(name = "medications_master")
public class MedicationsMaster {

    @Id
    @Column(name = "medication_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationID;

    @Column(name = "scientific_name ", nullable = false)
    @NotNull(message = "scientific Name  cannot be blank")
    private String scientificName ;

    @Column(name = "international_name ", nullable = false)
    @NotNull(message = "international Name  cannot be blank")
    private String internationalName  ;

    @Column(name = "medication_details ", columnDefinition = "TEXT")
    private String medicationDetails  ;

}
