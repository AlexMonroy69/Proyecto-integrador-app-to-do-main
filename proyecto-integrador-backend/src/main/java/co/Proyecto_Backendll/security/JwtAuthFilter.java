package co.Proyecto_Backendll.security;

import co.Proyecto_Backendll.store.AuthSession;
import co.Proyecto_Backendll.store.AuthSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Lee el header Authorization: Bearer, valida el JWT y comprueba que la sesion
 * siga activa (no revocada ni vencida) en cada peticion.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthSessionRepository sessions;

    public JwtAuthFilter(JwtService jwtService, AuthSessionRepository sessions) {
        this.jwtService = jwtService;
        this.sessions = sessions;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            Optional<JwtService.ParsedToken> parsed = jwtService.parse(token);
            if (parsed.isPresent()) {
                Optional<AuthSession> session = sessions.findById(parsed.get().sessionId());
                if (session.isPresent()
                        && !session.get().isRevoked()
                        && session.get().getExpiresAt().isAfter(Instant.now())
                        && session.get().getUser().getId().equals(parsed.get().userId())) {
                    AuthSession s = session.get();
                    AuthPrincipal principal = new AuthPrincipal(s.getUser().getId(), s.getId(), s.getUser().getRole());
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            principal, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + s.getUser().getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
