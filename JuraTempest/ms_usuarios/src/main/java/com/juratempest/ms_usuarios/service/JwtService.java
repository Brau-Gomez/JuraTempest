package com.juratempest.ms_usuarios.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
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

    // Constructor usado por Spring para inyectar ObjectMapper y valores configurados en application.properties.
    // Mantener secret y expiracion fuera del codigo permite cambiar seguridad sin recompilar la aplicacion.
    public JwtService(
        ObjectMapper objectMapper,
        @Value("${app.jwt.secret}") String secret
    ) {
        this.objectMapper = objectMapper;
        this.secret = secret;
    }

    // Valida estructura, firma y expiracion del token.
    // Retornamos false ante cualquier error para que la capa de seguridad rechace tokens corruptos o vencidos.
    public boolean esTokenValido(String token) {
        try {
            Map<String, Object> claims = obtenerClaims(token);
            Number exp = (Number) claims.get("exp");
            return exp.longValue() > Instant.now().getEpochSecond();
        } catch (Exception ex) {
            return false;
        }
    }

    // Extrae el email guardado en el claim sub del token.
    // Usamos sub porque en JWT representa el sujeto o identidad principal del token.
    public String obtenerEmail(String token) {
        return String.valueOf(obtenerClaims(token).get("sub"));
    }

    // Extrae los roles del token y los devuelve como Set para evitar duplicados.
    // Se contempla List porque al deserializar JSON Jackson suele convertir arreglos en listas.
    @SuppressWarnings("unchecked")
    public Set<String> obtenerRoles(String token) {
        Object roles = obtenerClaims(token).get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.stream().map(String::valueOf).collect(Collectors.toSet());
        }
        return ((Set<String>) roles);
    }

    // Lee y valida los claims internos del JWT.
    // Primero verifica cantidad de partes y firma; solo despues decodifica el payload para confiar en sus datos.
    private Map<String, Object> obtenerClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Token JWT invalido");
            }
            String expectedSignature = sign(parts[0] + "." + parts[1]);
            if (!expectedSignature.equals(parts[2])) {
                throw new IllegalArgumentException("Firma JWT invalida");
            }
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(payload, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("Token JWT invalido", ex);
        }
    }

    // Firma header y payload con HMAC-SHA256 usando el secreto configurado.
    // Esta firma permite detectar si alguien modifico el token despues de ser emitido.
    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }
}
