package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @Column(name = "doctor_id", nullable = false, unique = true)
    private Long doctorId;

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender cannot be blank")
    @Enumerated(EnumType.STRING)
    @JsonProperty("gender") // Maps the JSON property to the Java field
    private Gender gender;

    @Column(name = "bio")
    private String bio;


    @Column(name = "special_name", nullable = false, insertable = false, updatable = false)
    private String special_name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "special_name", referencedColumnName = "special_name")
    private Specialization specialization;



    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "UserID")
    private User user;
}
