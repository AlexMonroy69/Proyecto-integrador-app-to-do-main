package co.Proyecto_Backendll.api;

import co.Proyecto_Backendll.api.Dtos.*;
import co.Proyecto_Backendll.security.AuthPrincipal;
import co.Proyecto_Backendll.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(auth.register(req.username(), req.email(), req.password()));
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req.usernameOrEmail(), req.password());
    }

    @PostMapping("/auth/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return auth.refresh(req.refreshToken());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest req) {
        auth.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth/me")
    public CurrentUser me(@AuthenticationPrincipal AuthPrincipal principal) {
        return auth.me(principal);
    }

    @PutMapping("/account/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthPrincipal principal,
                                               @Valid @RequestBody ChangePasswordRequest req) {
        auth.changeOwnPassword(principal, req.currentPassword(), req.newPassword());
        return ResponseEntity.noContent().build();
    }
}
