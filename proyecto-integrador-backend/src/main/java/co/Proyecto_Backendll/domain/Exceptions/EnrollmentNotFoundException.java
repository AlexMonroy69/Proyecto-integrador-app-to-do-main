package co.Proyecto_Backendll.domain.Exceptions;

public class EnrollmentNotFoundException extends ResourceNotFoundException {

    public EnrollmentNotFoundException(Long id) {
        super("Enrollment not found with id: " + id);
    }

    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
