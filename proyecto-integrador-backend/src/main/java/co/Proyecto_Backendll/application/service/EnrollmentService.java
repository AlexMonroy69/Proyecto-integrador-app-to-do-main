package co.Proyecto_Backendll.application.service;

import co.Proyecto_Backendll.domain.Enrollment;
import co.Proyecto_Backendll.domain.EnrollmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    Enrollment create(Long studentId, Long courseId, LocalDate enrollmentDate);
    List<Enrollment> findAll();
    Optional<Enrollment> findById(Long id);
    Enrollment updateStatus(Long id, EnrollmentStatus status);
    void deleteById(Long id);
    boolean existsById(Long id);
}
