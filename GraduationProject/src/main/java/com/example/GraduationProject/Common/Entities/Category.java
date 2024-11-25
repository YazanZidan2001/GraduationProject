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
@Table(name = "categories")
public class Category {


    @Id
    @Column(name = "category_name", nullable = false)
    @NotNull(message = "category_name cannot be blank")
    private String category_name;
}
