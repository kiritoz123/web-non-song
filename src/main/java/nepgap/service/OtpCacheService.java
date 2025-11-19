package nepgap.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpCacheService {

    private static class OtpEntry {
        String otp;
        Instant expiresAt;

        public OtpEntry(String otp, long ttlSeconds) {
            this.otp = otp;
            this.expiresAt = Instant.now().plusSeconds(ttlSeconds);
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final Map<String, OtpEntry> cache = new ConcurrentHashMap<>();

    public void putOtp(String email, String otp, long ttlSeconds) {
        cache.put(email, new OtpEntry(otp, ttlSeconds));
    }

    public String getOtp(String email) {
        OtpEntry entry = cache.get(email);
        if (entry == null || entry.isExpired()) {
            cache.remove(email);
            return null;
        }
        return entry.otp;
    }

    public void removeOtp(String email) {
        cache.remove(email);
    }
}
