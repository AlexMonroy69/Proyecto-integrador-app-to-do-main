package co.Proyecto_Backendll.api;

import co.Proyecto_Backendll.store.AppUser;
import co.Proyecto_Backendll.store.Task;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/** Solicitudes y respuestas de la API /api (contrato del frontend). */
public final class Dtos {

    private Dtos() {}

    // ---------- Solicitudes ----------

    public record RegisterRequest(
            @NotBlank(message = "El nombre de usuario es obligatorio")
            @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
            String username,
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato válido")
            @Size(max = 100, message = "El correo no puede superar 100 caracteres")
            String email,
            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
            String password) {
    }

    public record LoginRequest(
            @NotBlank(message = "El usuario o correo es obligatorio") String usernameOrEmail,
            @NotBlank(message = "La contraseña es obligatoria") String password) {
    }

    public record RefreshRequest(
            @NotBlank(message = "El refresh token es obligatorio") String refreshToken) {
    }

    public record ChangePasswordRequest(
            @NotBlank(message = "La contraseña actual es obligatoria") String currentPassword,
            @NotBlank(message = "La nueva contraseña es obligatoria")
            @Size(min = 8, max = 72, message = "La nueva contraseña debe tener entre 8 y 72 caracteres")
            String newPassword) {
    }

    public record UserUpdateRequest(
            @NotBlank(message = "El nombre de usuario es obligatorio")
            @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
            String username,
            @NotBlank(message = "El correo es obligatorio")
            @Email(message = "El correo no tiene un formato válido")
            @Size(max = 100, message = "El correo no puede superar 100 caracteres")
            String email) {
    }

    public record PasswordResetRequest(
            @NotBlank(message = "La nueva contraseña es obligatoria")
            @Size(min = 8, max = 72, message = "La nueva contraseña debe tener entre 8 y 72 caracteres")
            String newPassword) {
    }

    /** completed omitido = false; completed explicitamente null = invalido. */
    public static class TaskRequest {

        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
        private String title;

        @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
        private String description;

        @NotNull(message = "El estado completed no puede ser nulo")
        private Boolean completed = false;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }

    // ---------- Respuestas ----------

    public record AuthResponse(String token, String type, String refreshToken, long expiresInSeconds,
                               Long id, String username, String email, String role) {
    }

    public record CurrentUser(Long id, String username, String email, String role) {
    }

    public record UserResponse(Long id, String username, String email) {
        public static UserResponse from(AppUser u) {
            return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
        }
    }

    public record TaskResponse(Long id, String title, String description, boolean completed,
                               LocalDateTime createdAt) {
        public static TaskResponse from(Task t) {
            return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.isCompleted(), t.getCreatedAt());
        }
    }

    public record PageResponse<T>(List<T> items, int page, int size, long totalItems, int totalPages) {
    }

    public record ApiError(LocalDateTime timestamp, int status, String error, List<String> details) {
    }
}
