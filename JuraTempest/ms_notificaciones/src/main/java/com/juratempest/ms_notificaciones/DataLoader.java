package com.juratempest.ms_notificaciones;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.juratempest.ms_notificaciones.model.Notificacion;
import com.juratempest.ms_notificaciones.repository.NotificacionRepository;

import net.datafaker.Faker;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

        if (notificacionRepository.count() > 0) {
            return;
        }

        List<String> tipos = List.of(
            "RESERVA",
            "PAGO",
            "MANTENIMIENTO",
            "TORNEO",
            "PROMOCION",
            "SISTEMA"
        );

        List<String> canales = List.of(
            "SISTEMA",
            "EMAIL",
            "SMS_SIMULADO",
            "WHATSAPP_SIMULADO"
        );

        for (int i = 0; i < 30; i++) {
            Notificacion notificacion = new Notificacion();
            String tipo = tipos.get(random.nextInt(tipos.size()));
            notificacion.setUsuarioId(faker.number().numberBetween(1L, 6L));
            notificacion.setTitulo(generarTitulo(faker, tipo));
            notificacion.setMensaje(faker.lorem().sentence(12));
            notificacion.setTipo(tipo);
            notificacion.setCanal(canales.get(random.nextInt(canales.size())));
            notificacion.setLeida(faker.bool().bool());
            notificacion.setFechaCreacion(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30)));

            notificacionRepository.save(notificacion);
        }
    }

    private String generarTitulo(Faker faker, String tipo) {
        return switch (tipo) {
            case "RESERVA" -> "Reserva confirmada";
            case "PAGO" -> "Pago registrado";
            case "MANTENIMIENTO" -> "Mantenimiento programado";
            case "TORNEO" -> "Nuevo torneo disponible";
            case "PROMOCION" -> "Promocion especial";
            case "SISTEMA" -> "Aviso del sistema";
            default -> faker.lorem().sentence(3);
        };
    }
}
