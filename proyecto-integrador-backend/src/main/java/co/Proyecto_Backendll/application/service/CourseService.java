package co.Proyecto_Backendll.application.service;

import co.Proyecto_Backendll.domain.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Course create(Course course);
    List<Course> findAll();
    Optional<Course> findById(Long id);
    Optional<Course> update(Course course);
    void deleteById(Long id);
    boolean existsById(Long id);
}
