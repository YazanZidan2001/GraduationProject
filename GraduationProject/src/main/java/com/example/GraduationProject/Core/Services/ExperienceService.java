package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.CompositeKey.ExperienceId;
import com.example.GraduationProject.Common.Entities.Experience;
import com.example.GraduationProject.Core.Repositories.ExperienceRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;

    public Experience addExperience(Experience experience) {
        return experienceRepository.save(experience);
    }

    public List<Experience> getExperiencesByDoctorId(Long doctorId) throws NotFoundException {
        List<Experience> experiences = experienceRepository.findByDoctorId(doctorId);
        if (experiences.isEmpty()) {
            throw new NotFoundException("No experiences found for doctor ID " + doctorId);
        }
        return experiences;
    }

    public Experience getExperienceById(ExperienceId experienceId) throws NotFoundException {
        return experienceRepository.findById(experienceId)
                .orElseThrow(() -> new NotFoundException("Experience not found for ID " + experienceId));
    }

    public Experience updateExperience(ExperienceId experienceId, Experience experience) throws NotFoundException {
        if (!experienceRepository.existsById(experienceId)) {
            throw new NotFoundException("Experience not found for ID " + experienceId);
        }
        return experienceRepository.save(experience);
    }

    public void deleteExperience(ExperienceId experienceId) throws NotFoundException {
        if (!experienceRepository.existsById(experienceId)) {
            throw new NotFoundException("Experience not found for ID " + experienceId);
        }
        experienceRepository.deleteById(experienceId);
    }

    public List<Experience> getAllExperiences() {
        return experienceRepository.findAll();
    }
}
