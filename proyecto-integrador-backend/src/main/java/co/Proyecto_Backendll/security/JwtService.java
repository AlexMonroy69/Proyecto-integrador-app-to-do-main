package co.Proyecto_Backendll.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Access tokens JWT (HS256) firmados con una clave secreta.
 * Claims: sub = id de usuario, sid = id de sesion, iat, exp.
 */
@Service
public class JwtService {

    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Pattern SUB = Pattern.compile("\"sub\":\"(\\d+)\"");
    private static final Pattern SID = Pattern.compile("\"sid\":\"([0-9a-fA-F-]{36})\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

    private final byte[] secret;
    private final long ttlSeconds;
    private final String encodedHeader;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-ttl-seconds:3600}") long ttlSeconds) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) {
            throw new IllegalStateException("app.jwt.secret debe tener al menos 32 caracteres");
        }
        this.ttlSeconds = ttlSeconds;
        this.encodedHeader = b64(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public String createAccessToken(Long userId, String sessionId) {
        long now = Instant.now().getEpochSecond();
        String payload = "{\"sub\":\"" + userId + "\",\"sid\":\"" + sessionId
                + "\",\"iat\":" + now + ",\"exp\":" + (now + ttlSeconds) + "}";
        String unsigned = encodedHeader + "." + b64(payload.getBytes(StandardCharsets.UTF_8));
        return unsigned + "." + b64(sign(unsigned));
    }

    /** Devuelve el usuario y la sesion del token si la firma y la vigencia son validas. */
    public Optional<ParsedToken> parse(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !encodedHeader.equals(parts[0])) {
                return Optional.empty();
            }
            byte[] expected = sign(parts[0] + "." + parts[1]);
            byte[] given = Base64.getUrlDecoder().decode(parts[2]);
            if (!MessageDigest.isEqual(expected, given)) {
                return Optional.empty();
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Matcher sub = SUB.matcher(payload);
            Matcher sid = SID.matcher(payload);
            Matcher exp = EXP.matcher(payload);
            if (!sub.find() || !sid.find() || !exp.find()) {
                return Optional.empty();
            }
            if (Long.parseLong(exp.group(1)) <= Instant.now().getEpochSecond()) {
                return Optional.empty();
            }
            return Optional.of(new ParsedToken(Long.parseLong(sub.group(1)), sid.group(1)));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private byte[] sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }

    private static String b64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record ParsedToken(Long userId, String sessionId) {
    }
}
