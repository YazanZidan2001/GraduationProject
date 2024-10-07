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
public class ExperienceId implements Serializable {
    private Long experienceId;
    private Long doctorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperienceId that = (ExperienceId) o;
        return Objects.equals(experienceId, that.experienceId) &&
                Objects.equals(doctorId, that.doctorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experienceId, doctorId);
    }
}
