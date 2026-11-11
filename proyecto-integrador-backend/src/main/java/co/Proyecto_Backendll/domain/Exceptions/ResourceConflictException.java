package co.Proyecto_Backendll.domain.Exceptions;

public class ResourceConflictException extends BusinessException {

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
