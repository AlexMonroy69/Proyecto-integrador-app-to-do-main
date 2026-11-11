package co.Proyecto_Backendll.domain.repository;

import co.Proyecto_Backendll.domain.Enrollment;
import co.Proyecto_Backendll.domain.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudent_IdAndCourse_IdAndStatusNot(Long studentId, Long courseId, EnrollmentStatus status);

    boolean existsByStudent_IdAndCourse_IdAndStatusNotAndIdNot(Long studentId, Long courseId, EnrollmentStatus status, Long id);

    long countByCourse_IdAndStatus(Long courseId, EnrollmentStatus status);
}
