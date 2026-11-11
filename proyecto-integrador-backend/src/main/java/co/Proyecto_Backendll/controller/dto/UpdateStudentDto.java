package co.Proyecto_Backendll.controller.dto;

import co.Proyecto_Backendll.domain.Student;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateStudentDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @Size(max = 20) String phone,
        @NotNull @Past
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate
) {
    public Student toStudent(Long id) {
        return new Student(id, firstName.trim(), lastName.trim(), email.trim().toLowerCase(), phone, birthDate);
    }
}
