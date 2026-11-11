package co.Proyecto_Backendll.controller;

import co.Proyecto_Backendll.application.service.StudentService;
import co.Proyecto_Backendll.controller.dto.CreateStudentDto;
import co.Proyecto_Backendll.controller.dto.Response.StudentResponseDto;
import co.Proyecto_Backendll.controller.dto.UpdateStudentDto;
import co.Proyecto_Backendll.domain.Exceptions.StudentNotFoundException;
import co.Proyecto_Backendll.domain.Student;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDto>> list() {
        List<StudentResponseDto> students = studentService.findAll()
                .stream()
                .map(StudentResponseDto::from)
                .toList();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getById(@PathVariable Long id) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return ResponseEntity.ok(StudentResponseDto.from(student));
    }

    @PostMapping
    public ResponseEntity<StudentResponseDto> create(@Valid @RequestBody CreateStudentDto createStudentDto) {
        Student student = studentService.create(createStudentDto.toStudent());
        return ResponseEntity.status(HttpStatus.CREATED).body(StudentResponseDto.from(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateStudentDto updateStudentDto) {
        Student updated = studentService.update(updateStudentDto.toStudent(id))
                .orElseThrow(() -> new StudentNotFoundException(id));
        return ResponseEntity.ok(StudentResponseDto.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
