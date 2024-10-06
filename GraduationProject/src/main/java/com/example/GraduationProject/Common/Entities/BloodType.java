package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.BloodTypes;
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
@Table(name = "blood_type")
public class BloodType {
    @Id
    @Column(name = "blood_type", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Blood type cannot be blank")
    private BloodTypes bloodType; // This is your primary key
}
