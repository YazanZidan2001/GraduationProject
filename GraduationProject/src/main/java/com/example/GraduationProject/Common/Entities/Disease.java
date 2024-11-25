package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.DiseaseType;
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
@Table(name = "disease")
public class Disease {

    @Id
    @Column(name = "disease_name", nullable = false)
    @NotNull(message = "Disease name cannot be blank")
    private String name; // Primary key and unique

    @Enumerated(EnumType.STRING)
    @Column(name = "disease_type", nullable = false)
    @NotNull(message = "Disease type cannot be blank")
    private DiseaseType diseaseType; // Enum for disease type

    // Store categoryName explicitly in the database for ease of use in JSON
    @Column(name = "category_name", nullable = false)
    @NotNull(message = "Category name cannot be blank")
    private String categoryName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_name", referencedColumnName = "category_name", insertable = false, updatable = false)
    private Category category; // Links to the Category entity

    @Column(name = "remarks")
    private String remarks; // Optional remarks
}
