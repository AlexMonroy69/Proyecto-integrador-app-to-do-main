package co.Proyecto_Backendll.security;

import co.Proyecto_Backendll.store.Role;

/** Usuario autenticado de la peticion actual. */
public record AuthPrincipal(Long userId, String sessionId, Role role) {
}
