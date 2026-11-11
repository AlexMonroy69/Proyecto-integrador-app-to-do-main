package co.Proyecto_Backendll.controller;

import co.Proyecto_Backendll.application.service.CourseService;
import co.Proyecto_Backendll.controller.dto.CreateCourseDto;
import co.Proyecto_Backendll.controller.dto.Response.CourseResponseDto;
import co.Proyecto_Backendll.controller.dto.UpdateCourseDto;
import co.Proyecto_Backendll.domain.Course;
import co.Proyecto_Backendll.domain.Exceptions.CourseNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> list() {
        List<CourseResponseDto> courses = courseService.findAll()
                .stream()
                .map(CourseResponseDto::from)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getById(@PathVariable Long id) {
        Course course = courseService.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return ResponseEntity.ok(CourseResponseDto.from(course));
    }

    @PostMapping
    public ResponseEntity<CourseResponseDto> create(@Valid @RequestBody CreateCourseDto createCourseDto) {
        Course course = courseService.create(createCourseDto.toCourse());
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponseDto.from(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateCourseDto updateCourseDto) {
        Course updated = courseService.update(updateCourseDto.toCourse(id))
                .orElseThrow(() -> new CourseNotFoundException(id));
        return ResponseEntity.ok(CourseResponseDto.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
