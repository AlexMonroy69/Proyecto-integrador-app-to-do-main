package co.Proyecto_Backendll.controller.dto.Response;

import co.Proyecto_Backendll.domain.Enrollment;
import co.Proyecto_Backendll.domain.EnrollmentStatus;

import java.time.LocalDate;

public record EnrollmentResponseDto(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseName,
        LocalDate enrollmentDate,
        EnrollmentStatus status
) {
    public static EnrollmentResponseDto from(Enrollment enrollment) {
        return new EnrollmentResponseDto(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getName(),
                enrollment.getEnrollmentDate(),
                enrollment.getStatus()
        );
    }
}
