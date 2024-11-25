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
@Table(name = "specialization")
public class Specialization {

    @Id
    @Column(name = "special_name", nullable = false)
    @NotNull(message = "special_name cannot be blank")
    private String special_name; // This should match exactly

    // Store categoryName explicitly in the database for ease of use in JSON
    @Column(name = "category_name", nullable = false)
    @NotNull(message = "Category name cannot be blank")
    private String categoryName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_name", referencedColumnName = "category_name", insertable = false, updatable = false)
    private Category category; // Links to the Category entity

    public boolean isEmpty() {
        return this.special_name == null || this.special_name.isEmpty();
    }
}
