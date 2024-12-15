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
@Table(name = "procedure_master")
public class ProcedureMaster {


    @Id
    @Column(name = "procedure_name", nullable = false)
    @NotNull(message = "Procedure name cannot be blank")
    private String procedure_name;


    @Column(name = "procedure_description", nullable = false)
    @NotNull(message = "Procedure Description cannot be blank")
    private String procedure_description;
}
