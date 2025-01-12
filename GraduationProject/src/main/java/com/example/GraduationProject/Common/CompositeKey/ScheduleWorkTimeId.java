package com.example.GraduationProject.Common.CompositeKey;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ScheduleWorkTimeId implements Serializable {

    private Long scheduleId;

    private Long doctorId;

    private Long clinicId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleWorkTimeId that = (ScheduleWorkTimeId) o;
        return Objects.equals(scheduleId, that.scheduleId) &&
                Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(clinicId, that.clinicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId, doctorId, clinicId);
    }
}
