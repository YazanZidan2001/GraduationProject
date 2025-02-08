package com.example.GraduationProject.Common.Converters;

import com.example.GraduationProject.Common.Enums.DaysOfWeek;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class DaysOfWeekConverter implements AttributeConverter<List<DaysOfWeek>, String> {
    @Override
    public String convertToDatabaseColumn(List<DaysOfWeek> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream()
                .map(DaysOfWeek::name)
                .collect(Collectors.joining(",")); // Store as "MONDAY,TUESDAY"
    }

    @Override
    public List<DaysOfWeek> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Arrays.stream(dbData.split(","))
                .map(DaysOfWeek::valueOf)
                .collect(Collectors.toList());
    }
}
