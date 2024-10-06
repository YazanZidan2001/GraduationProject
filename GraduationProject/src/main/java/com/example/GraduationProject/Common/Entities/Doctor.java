package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "doctor")
public class Doctor {

    @Id
    @Column(name = "doctor_id", nullable = false, unique = true)
    private Long doctorId;

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender cannot be blank")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "bio")
    private String bio;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = "specialization cannot be blank")
    @JoinColumn(name = "special_name", referencedColumnName = "special_name", nullable = false)
    @JsonProperty("specialization") // Maps the JSON property to the Java field
    private Specialization specialization; // Reference to Specialization

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "UserID")
    private User user;
}
