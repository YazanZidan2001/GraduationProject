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
public class AppointmentCompositeKey implements Serializable {

    private Long appointmentID;
    private Long doctorID;
    private Long patientID;
    private Long clinicID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentCompositeKey that = (AppointmentCompositeKey) o;
        return Objects.equals(appointmentID, that.appointmentID) &&
                Objects.equals(doctorID, that.doctorID) &&
                Objects.equals(patientID, that.patientID) &&
                Objects.equals(clinicID, that.clinicID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentID, doctorID, patientID, clinicID);
    }
}
