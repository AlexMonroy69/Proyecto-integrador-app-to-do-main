package co.Proyecto_Backendll.common;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Error de negocio de la API /api. Se traduce a la respuesta
 * { timestamp, status, error, details } descrita en el contrato del frontend.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final List<String> details;
    private final boolean emptyBody;

    private ApiException(HttpStatus status, String message, boolean emptyBody) {
        super(message);
        this.status = status;
        this.details = List.of(message);
        this.emptyBody = emptyBody;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message, false);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message, false);
    }

    /** 401 sin cuerpo (refresh token invalido, vencido o reutilizado). */
    public static ApiException unauthorizedEmpty() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "No autorizado", true);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message, false);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message, false);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getDetails() {
        return details;
    }

    public boolean isEmptyBody() {
        return emptyBody;
    }
}
