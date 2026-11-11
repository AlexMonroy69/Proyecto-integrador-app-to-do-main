package co.Proyecto_Backendll.domain.Exceptions;

public class CourseNotFoundException extends ResourceNotFoundException {

    public CourseNotFoundException(Long id) {
        super("Course not found with id: " + id);
    }

    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
