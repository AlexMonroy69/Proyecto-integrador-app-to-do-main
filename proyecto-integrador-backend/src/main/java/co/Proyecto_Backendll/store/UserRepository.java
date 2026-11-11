package co.Proyecto_Backendll.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    boolean existsByRole(Role role);

    /** Indica si otro usuario (distinto de excludeId) ya usa el valor como username o email. */
    @Query("select count(u) > 0 from AppUser u "
            + "where (lower(u.username) = lower(:value) or lower(u.email) = lower(:value)) "
            + "and u.id <> :excludeId")
    boolean identifierTaken(@Param("value") String value, @Param("excludeId") Long excludeId);

    @Query(value = "select u from AppUser u "
            + "where lower(u.username) like lower(:pattern) or lower(u.email) like lower(:pattern)",
            countQuery = "select count(u) from AppUser u "
            + "where lower(u.username) like lower(:pattern) or lower(u.email) like lower(:pattern)")
    Page<AppUser> search(@Param("pattern") String pattern, Pageable pageable);
}
