package co.Proyecto_Backendll.controller.dto;

import co.Proyecto_Backendll.domain.EnrollmentStatus;
import jakarta.validation.constraints.NotBlank;

public record UpdateEnrollmentDto(@NotBlank String status) {

    public EnrollmentStatus toStatus() {
        return EnrollmentStatus.from(status);
    }
}
