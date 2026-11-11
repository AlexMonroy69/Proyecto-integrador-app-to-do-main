package co.Proyecto_Backendll.service;

import co.Proyecto_Backendll.store.AppUser;
import co.Proyecto_Backendll.store.Role;
import co.Proyecto_Backendll.store.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Crea el primer usuario ADMIN al arrancar si todavia no existe ninguno.
 * Configurable con ADMIN_USERNAME, ADMIN_EMAIL y ADMIN_PASSWORD.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository users;
    private final UserService userService;
    private final String username;
    private final String email;
    private final String password;

    public AdminSeeder(UserRepository users, UserService userService,
                       @Value("${app.admin.username}") String username,
                       @Value("${app.admin.email}") String email,
                       @Value("${app.admin.password}") String password) {
        this.users = users;
        this.userService = userService;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.existsByRole(Role.ADMIN)) {
            return;
        }
        AppUser admin = userService.createUser(username, email, password, Role.ADMIN);
        log.info("Usuario ADMIN inicial creado: {}", admin.getUsername());
    }
}
