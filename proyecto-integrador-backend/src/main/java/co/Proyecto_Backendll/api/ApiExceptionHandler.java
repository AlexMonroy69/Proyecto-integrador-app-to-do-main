package co.Proyecto_Backendll.api;

import co.Proyecto_Backendll.api.Dtos.ApiError;
import co.Proyecto_Backendll.common.ApiException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Errores de los controladores /api con la forma { timestamp, status, error, details }.
 * Se limita al paquete api para no cambiar el formato de errores de students/courses/enrollments.
 */
@RestControllerAdvice(basePackages = "co.Proyecto_Backendll.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex) {
        if (ex.isEmptyBody()) {
            return ResponseEntity.status(ex.getStatus()).build();
        }
        return build(ex.getStatus(), ex.getStatus() == HttpStatus.UNAUTHORIZED ? "No autorizado" : ex.getMessage(),
                ex.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage() == null ? "Valor inválido" : e.getDefaultMessage())
                .distinct()
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Error de validación", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Solicitud inválida",
                List.of("El cuerpo de la solicitud falta o tiene un formato JSON inválido"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parámetro inválido",
                List.of("Valor inválido para el parámetro '" + ex.getName() + "'"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parámetro inválido",
                List.of("Falta el parámetro '" + ex.getParameterName() + "'"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Conflicto",
                List.of("La operación entra en conflicto con datos existentes"));
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, List<String> details) {
        return ResponseEntity.status(status)
                .body(new ApiError(LocalDateTime.now(), status.value(), error, details));
    }
}
