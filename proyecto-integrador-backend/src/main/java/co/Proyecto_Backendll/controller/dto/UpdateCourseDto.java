package co.Proyecto_Backendll.controller.dto;

import co.Proyecto_Backendll.domain.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCourseDto(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotNull @Positive Integer maxCapacity
) {
    public Course toCourse(Long id) {
        return new Course(id, code.trim().toUpperCase(), name.trim(), description, maxCapacity);
    }
}
