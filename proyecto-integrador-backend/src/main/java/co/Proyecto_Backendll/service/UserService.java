package co.Proyecto_Backendll.service;

import co.Proyecto_Backendll.api.Dtos.PageResponse;
import co.Proyecto_Backendll.api.Dtos.UserResponse;
import co.Proyecto_Backendll.common.ApiException;
import co.Proyecto_Backendll.store.*;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(noRollbackFor = ApiException.class)
public class UserService {

    private static final Set<String> SORTS = Set.of("username", "email");

    private final UserRepository users;
    private final TaskRepository tasks;
    private final AuthSessionRepository sessions;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, TaskRepository tasks, AuthSessionRepository sessions,
                       PasswordEncoder encoder) {
        this.users = users;
        this.tasks = tasks;
        this.sessions = sessions;
        this.encoder = encoder;
    }

    public AppUser createUser(String username, String email, String password, Role role) {
        String u = username.trim();
        String e = email.trim().toLowerCase();
        ensureAvailable(u, e, -1L);
        return users.save(new AppUser(u, e, encoder.encode(password), role));
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(String q, int page, int size, String sort, String dir) {
        Page<AppUser> result = users.search(PageSupport.pattern(q),
                PageSupport.pageRequest(page, size, sort, dir, SORTS));
        return PageSupport.toResponse(result, UserResponse::from);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return UserResponse.from(find(id));
    }

    public UserResponse create(String username, String email, String password) {
        return UserResponse.from(createUser(username, email, password, Role.USER));
    }

    public UserResponse update(Long id, String username, String email) {
        AppUser user = find(id);
        String u = username.trim();
        String e = email.trim().toLowerCase();
        ensureAvailable(u, e, id);
        user.setUsername(u);
        user.setEmail(e);
        users.save(user);
        sessions.revokeAllByUserId(id);
        return UserResponse.from(user);
    }

    public void delete(Long id) {
        AppUser user = find(id);
        if (tasks.existsByOwnerId(id)) {
            throw ApiException.conflict("No se puede eliminar un usuario con tareas asociadas");
        }
        sessions.deleteAllByUserId(id);
        users.delete(user);
    }

    public void resetPassword(Long id, String newPassword) {
        AppUser user = find(id);
        user.setPasswordHash(encoder.encode(newPassword));
        users.save(user);
        sessions.revokeAllByUserId(id);
    }

    private AppUser find(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("Usuario no encontrado"));
    }

    /** Ni el username ni el email pueden coincidir con el username/email de otra cuenta. */
    private void ensureAvailable(String username, String email, Long excludeId) {
        if (users.identifierTaken(username, excludeId)) {
            throw ApiException.badRequest("El nombre de usuario ya está en uso");
        }
        if (users.identifierTaken(email, excludeId)) {
            throw ApiException.badRequest("El correo electrónico ya está en uso");
        }
    }
}
