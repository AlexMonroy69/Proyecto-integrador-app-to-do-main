package co.Proyecto_Backendll.application.service.Impl;

import co.Proyecto_Backendll.application.service.StudentService;
import co.Proyecto_Backendll.domain.Exceptions.StudentEmailAlreadyExistsException;
import co.Proyecto_Backendll.domain.Exceptions.StudentNotFoundException;
import co.Proyecto_Backendll.domain.Student;
import co.Proyecto_Backendll.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student create(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new StudentEmailAlreadyExistsException(student.getEmail());
        }
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> update(Student student) {
        Optional<Student> existing = studentRepository.findById(student.getId());
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        if (studentRepository.existsByEmailAndIdNot(student.getEmail(), student.getId())) {
            throw new StudentEmailAlreadyExistsException(student.getEmail());
        }

        Student current = existing.get();
        current.setFirstName(student.getFirstName());
        current.setLastName(student.getLastName());
        current.setEmail(student.getEmail());
        current.setPhone(student.getPhone());
        current.setBirthDate(student.getBirthDate());

        return Optional.of(studentRepository.save(current));
    }

    @Override
    public void deleteById(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }
}
