package com.juratempest.ms_mantenimiento.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final ObjectMapper objectMapper;
    private final String secret;
    public JwtService(ObjectMapper objectMapper, @Value("${app.jwt.secret}") String secret) { this.objectMapper = objectMapper; this.secret = secret; }
    public boolean esTokenValido(String token) {
        try { Map<String, Object> claims = obtenerClaims(token); Number exp = (Number) claims.get("exp"); return exp != null && exp.longValue() > Instant.now().getEpochSecond(); }
        catch (Exception ex) { return false; }
    }
    public String obtenerEmail(String token) { return String.valueOf(obtenerClaims(token).get("sub")); }
    @SuppressWarnings("unchecked")
    public Set<String> obtenerRoles(String token) {
        Object roles = obtenerClaims(token).get("roles");
        if (roles instanceof List<?> roleList) return roleList.stream().map(String::valueOf).collect(Collectors.toSet());
        return ((Set<String>) roles);
    }
    private Map<String, Object> obtenerClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Token JWT invalido");
            String expectedSignature = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) throw new IllegalArgumentException("Firma JWT invalida");
            return objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {});
        } catch (Exception ex) { throw new IllegalArgumentException("Token JWT invalido", ex); }
    }
    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }
}
