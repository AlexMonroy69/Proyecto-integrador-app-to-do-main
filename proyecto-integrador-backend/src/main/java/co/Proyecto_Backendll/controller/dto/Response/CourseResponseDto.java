package co.Proyecto_Backendll.controller.dto.Response;

import co.Proyecto_Backendll.domain.Course;

public record CourseResponseDto(
        Long id,
        String code,
        String name,
        String description,
        Integer maxCapacity
) {
    public static CourseResponseDto from(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getMaxCapacity());
    }
}

