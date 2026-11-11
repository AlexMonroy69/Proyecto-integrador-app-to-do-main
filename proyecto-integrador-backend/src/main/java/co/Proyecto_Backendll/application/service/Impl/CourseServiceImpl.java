package co.Proyecto_Backendll.application.service.Impl;

import co.Proyecto_Backendll.application.service.CourseService;
import co.Proyecto_Backendll.domain.Course;
import co.Proyecto_Backendll.domain.EnrollmentStatus;
import co.Proyecto_Backendll.domain.Exceptions.CourseNotFoundException;
import co.Proyecto_Backendll.domain.Exceptions.ResourceConflictException;
import co.Proyecto_Backendll.domain.repository.CourseRepository;
import co.Proyecto_Backendll.domain.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseServiceImpl(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Course create(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new ResourceConflictException("Course with code: " + course.getCode() + " already exists");
        }
        return courseRepository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public Optional<Course> update(Course course) {
        Optional<Course> existing = courseRepository.findById(course.getId());
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        if (courseRepository.existsByCodeAndIdNot(course.getCode(), course.getId())) {
            throw new ResourceConflictException("Course with code: " + course.getCode() + " already exists");
        }

        long activeEnrollments = enrollmentRepository.countByCourse_IdAndStatus(course.getId(), EnrollmentStatus.ACTIVE);
        if (course.getMaxCapacity() < activeEnrollments) {
            throw new ResourceConflictException("Max capacity cannot be lower than the " + activeEnrollments
                    + " active enrollments of the course");
        }

        Course current = existing.get();
        current.setCode(course.getCode());
        current.setName(course.getName());
        current.setDescription(course.getDescription());
        current.setMaxCapacity(course.getMaxCapacity());

        return Optional.of(courseRepository.save(current));
    }

    @Override
    public void deleteById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new CourseNotFoundException(id);
        }
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }
}
