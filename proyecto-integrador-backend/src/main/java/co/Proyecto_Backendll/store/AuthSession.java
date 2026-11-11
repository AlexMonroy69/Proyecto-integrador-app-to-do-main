package co.Proyecto_Backendll.store;

import jakarta.persistence.*;

import java.time.Instant;

/** Sesion de login: un refresh token rotativo y su estado de revocacion. */
@Entity
@Table(name = "auth_sessions")
public class AuthSession {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "refresh_hash", nullable = false, length = 64)
    private String refreshHash;

    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    public AuthSession() {}

    public AuthSession(String id, AppUser user, String refreshHash, Instant expiresAt) {
        this.id = id;
        this.user = user;
        this.refreshHash = refreshHash;
        this.expiresAt = expiresAt;
    }

    public String getId() { return id; }
    public AppUser getUser() { return user; }
    public String getRefreshHash() { return refreshHash; }
    public void setRefreshHash(String refreshHash) { this.refreshHash = refreshHash; }
    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
