package co.Proyecto_Backendll.application.service;

import co.Proyecto_Backendll.domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student create(Student student);
    List<Student> findAll();
    Optional<Student> findById(Long id);
    Optional<Student> update(Student student);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByEmail(String email);
}
