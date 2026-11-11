package co.Proyecto_Backendll.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateEnrollmentDto(
        @NotNull Long studentId,
        @NotNull Long courseId,
        @PastOrPresent
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate enrollmentDate
) {
}
