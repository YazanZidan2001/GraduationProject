package com.example.GraduationProject.Common.CompositeKey;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class VisitCompositeKey implements Serializable {

    private Long visitID;
    private Long clinicId;
    private Long doctorId;
    private Long patientId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitCompositeKey that = (VisitCompositeKey) o;
        return Objects.equals(visitID, that.visitID) &&
                Objects.equals(clinicId, that.clinicId) &&
                Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(patientId, that.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitID, clinicId, doctorId, patientId);
    }
}

