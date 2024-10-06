package com.example.GraduationProject.Common.CompositeKey;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PatientDiseaseId implements Serializable {

    private Long patientId;

    private String diseaseName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientDiseaseId that = (PatientDiseaseId) o;
        return Objects.equals(patientId, that.patientId) &&
                Objects.equals(diseaseName, that.diseaseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, diseaseName);
    }
}
