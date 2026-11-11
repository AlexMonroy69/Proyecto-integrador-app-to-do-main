package co.Proyecto_Backendll.service;

import co.Proyecto_Backendll.api.Dtos.AuthResponse;
import co.Proyecto_Backendll.api.Dtos.CurrentUser;
import co.Proyecto_Backendll.common.ApiException;
import co.Proyecto_Backendll.security.AuthPrincipal;
import co.Proyecto_Backendll.security.JwtService;
import co.Proyecto_Backendll.store.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Registro, login, refresh rotativo, logout y cambio de contraseña propio.
 * noRollbackFor: la revocacion por reuso del refresh token debe confirmarse
 * aunque despues se lance la excepcion 401.
 */
@Service
@Transactional(noRollbackFor = ApiException.class)
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository users;
    private final AuthSessionRepository sessions;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final UserService userService;
    private final Duration refreshTtl;

    public AuthService(UserRepository users, AuthSessionRepository sessions, PasswordEncoder encoder,
                       JwtService jwt, UserService userService,
                       @Value("${app.jwt.refresh-ttl-hours:24}") long refreshTtlHours) {
        this.users = users;
        this.sessions = sessions;
        this.encoder = encoder;
        this.jwt = jwt;
        this.userService = userService;
        this.refreshTtl = Duration.ofHours(refreshTtlHours);
    }

    public AuthResponse register(String username, String email, String password) {
        AppUser user = userService.createUser(username, email, password, Role.USER);
        return startSession(user);
    }

    public AuthResponse login(String usernameOrEmail, String password) {
        String id = usernameOrEmail.trim();
        Optional<AppUser> found = users.findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(id, id);
        if (found.isEmpty() || !encoder.matches(password, found.get().getPasswordHash())) {
            throw ApiException.unauthorized("Credenciales inválidas");
        }
        return startSession(found.get());
    }

    public AuthResponse refresh(String refreshToken) {
        String hash = sha256(refreshToken);
        Optional<AuthSession> current = sessions.findByRefreshHash(hash);
        if (current.isEmpty()) {
            // Un token anterior ya rotado: posible robo. Se revoca toda la sesion.
            sessions.findByPreviousHash(hash).ifPresent(s -> {
                s.setRevoked(true);
                sessions.save(s);
            });
            throw ApiException.unauthorizedEmpty();
        }
        AuthSession session = current.get();
        if (session.isRevoked() || !session.getExpiresAt().isAfter(Instant.now())) {
            throw ApiException.unauthorizedEmpty();
        }
        String newRefresh = newRefreshToken();
        session.setPreviousHash(session.getRefreshHash());
        session.setRefreshHash(sha256(newRefresh));
        session.setExpiresAt(Instant.now().plus(refreshTtl));
        sessions.save(session);
        return buildResponse(session.getUser(), session.getId(), newRefresh);
    }

    public void logout(String refreshToken) {
        String hash = sha256(refreshToken);
        Optional<AuthSession> session = sessions.findByRefreshHash(hash);
        if (session.isEmpty()) {
            session = sessions.findByPreviousHash(hash);
        }
        session.ifPresent(s -> {
            s.setRevoked(true);
            sessions.save(s);
        });
    }

    @Transactional(readOnly = true)
    public CurrentUser me(AuthPrincipal principal) {
        AppUser u = users.findById(principal.userId()).orElseThrow(ApiException::unauthorizedEmpty);
        return new CurrentUser(u.getId(), u.getUsername(), u.getEmail(), u.getRole().name());
    }

    public void changeOwnPassword(AuthPrincipal principal, String currentPassword, String newPassword) {
        AppUser u = users.findById(principal.userId()).orElseThrow(ApiException::unauthorizedEmpty);
        if (!encoder.matches(currentPassword, u.getPasswordHash())) {
            throw ApiException.unauthorized("La contraseña actual es incorrecta");
        }
        u.setPasswordHash(encoder.encode(newPassword));
        users.save(u);
        sessions.revokeAllByUserId(u.getId());
    }

    // ---------- internos ----------

    private AuthResponse startSession(AppUser user) {
        String refresh = newRefreshToken();
        AuthSession session = new AuthSession(UUID.randomUUID().toString(), user, sha256(refresh),
                Instant.now().plus(refreshTtl));
        sessions.save(session);
        return buildResponse(user, session.getId(), refresh);
    }

    private AuthResponse buildResponse(AppUser user, String sessionId, String refreshToken) {
        return new AuthResponse(jwt.createAccessToken(user.getId(), sessionId), "Bearer", refreshToken,
                jwt.getTtlSeconds(), user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    private static String newRefreshToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
