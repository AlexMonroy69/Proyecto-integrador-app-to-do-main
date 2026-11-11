package co.Proyecto_Backendll.domain.Exceptions;

public class StudentNotFoundException extends ResourceNotFoundException {
    public StudentNotFoundException(Long id) {
        super("Student not found with id: " + id);
    }
}
