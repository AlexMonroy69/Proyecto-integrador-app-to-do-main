package co.Proyecto_Backendll.application.service.Impl;

import co.Proyecto_Backendll.application.service.EnrollmentService;
import co.Proyecto_Backendll.domain.Course;
import co.Proyecto_Backendll.domain.Enrollment;
import co.Proyecto_Backendll.domain.EnrollmentStatus;
import co.Proyecto_Backendll.domain.Exceptions.BusinessException;
import co.Proyecto_Backendll.domain.Exceptions.CourseNotFoundException;
import co.Proyecto_Backendll.domain.Exceptions.EnrollmentNotFoundException;
import co.Proyecto_Backendll.domain.Exceptions.ResourceConflictException;
import co.Proyecto_Backendll.domain.Exceptions.StudentNotFoundException;
import co.Proyecto_Backendll.domain.Student;
import co.Proyecto_Backendll.domain.repository.CourseRepository;
import co.Proyecto_Backendll.domain.repository.EnrollmentRepository;
import co.Proyecto_Backendll.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Enrollment create(Long studentId, Long courseId, LocalDate enrollmentDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (enrollmentRepository.existsByStudent_IdAndCourse_IdAndStatusNot(studentId, courseId, EnrollmentStatus.CANCELLED)) {
            throw new ResourceConflictException("Student " + studentId + " is already enrolled in course " + courseId);
        }
        checkAvailableSpots(course);

        LocalDate date = enrollmentDate != null ? enrollmentDate : LocalDate.now();
        return enrollmentRepository.save(new Enrollment(null, student, course, date, EnrollmentStatus.ACTIVE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public Enrollment updateStatus(Long id, EnrollmentStatus newStatus) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException(id));
        EnrollmentStatus currentStatus = enrollment.getStatus();

        if (currentStatus == newStatus) {
            return enrollment;
        }
        if (currentStatus == EnrollmentStatus.COMPLETE) {
            throw new BusinessException("A completed enrollment cannot be modified");
        }
        if (currentStatus == EnrollmentStatus.CANCELLED && newStatus == EnrollmentStatus.COMPLETE) {
            throw new BusinessException("A cancelled enrollment cannot be completed");
        }

        if (newStatus == EnrollmentStatus.ACTIVE) {
            Long studentId = enrollment.getStudent().getId();
            Long courseId = enrollment.getCourse().getId();
            if (enrollmentRepository.existsByStudent_IdAndCourse_IdAndStatusNotAndIdNot(
                    studentId, courseId, EnrollmentStatus.CANCELLED, id)) {
                throw new ResourceConflictException("Student " + studentId + " is already enrolled in course " + courseId);
            }
            checkAvailableSpots(enrollment.getCourse());
        }

        enrollment.setStatus(newStatus);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteById(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new EnrollmentNotFoundException(id);
        }
        enrollmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return enrollmentRepository.existsById(id);
    }

    private void checkAvailableSpots(Course course) {
        if (course.getMaxCapacity() == null) {
            return;
        }
        long active = enrollmentRepository.countByCourse_IdAndStatus(course.getId(), EnrollmentStatus.ACTIVE);
        if (active >= course.getMaxCapacity()) {
            throw new ResourceConflictException("Course " + course.getCode() + " has no available spots");
        }
    }
}
