package co.Proyecto_Backendll.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, String> {

    Optional<AuthSession> findByRefreshHash(String refreshHash);

    Optional<AuthSession> findByPreviousHash(String previousHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AuthSession s set s.revoked = true where s.user.id = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from AuthSession s where s.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
