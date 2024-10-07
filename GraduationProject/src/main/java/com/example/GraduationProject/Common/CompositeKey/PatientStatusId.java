package com.example.GraduationProject.Common.CompositeKey;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PatientStatusId implements Serializable {

    private Long patientId;
    private String description;  // Assuming description is unique per patient

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientStatusId that = (PatientStatusId) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, description);
    }
}
