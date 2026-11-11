package co.Proyecto_Backendll.controller;

import co.Proyecto_Backendll.application.service.EnrollmentService;
import co.Proyecto_Backendll.controller.dto.CreateEnrollmentDto;
import co.Proyecto_Backendll.controller.dto.Response.EnrollmentResponseDto;
import co.Proyecto_Backendll.controller.dto.UpdateEnrollmentDto;
import co.Proyecto_Backendll.domain.Enrollment;
import co.Proyecto_Backendll.domain.Exceptions.EnrollmentNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDto>> list() {
        List<EnrollmentResponseDto> enrollments = enrollmentService.findAll()
                .stream()
                .map(EnrollmentResponseDto::from)
                .toList();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> getById(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException(id));
        return ResponseEntity.ok(EnrollmentResponseDto.from(enrollment));
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> create(@Valid @RequestBody CreateEnrollmentDto dto) {
        Enrollment enrollment = enrollmentService.create(dto.studentId(), dto.courseId(), dto.enrollmentDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnrollmentResponseDto.from(enrollment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDto> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateEnrollmentDto dto) {
        Enrollment enrollment = enrollmentService.updateStatus(id, dto.toStatus());
        return ResponseEntity.ok(EnrollmentResponseDto.from(enrollment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
