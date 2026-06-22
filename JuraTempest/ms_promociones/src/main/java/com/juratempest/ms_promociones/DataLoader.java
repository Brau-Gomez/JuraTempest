package com.juratempest.ms_promociones;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.juratempest.ms_promociones.model.Promocion;
import com.juratempest.ms_promociones.repository.PromocionRepository;

import net.datafaker.Faker;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private final PromocionRepository repository;

    public DataLoader(PromocionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();
        List<String> tipos = List.of("GENERAL", "USUARIO_FRECUENTE", "HORARIO_BAJA_DEMANDA", "TORNEO", "FIDELIZACION");

        for (int i = 1; i <= 25; i++) {
            LocalDate inicio = LocalDate.now().minusDays(faker.number().numberBetween(0, 20));
            LocalDate fin = LocalDate.now().plusDays(faker.number().numberBetween(15, 90));

            Promocion promocion = Promocion.builder()
                    .codigo("PROMO" + String.format("%02d", i))
                    .nombre("Promo " + faker.commerce().promotionCode())
                    .descripcion("Descuento especial " + faker.commerce().productName())
                    .porcentajeDescuento(faker.number().numberBetween(5, 31))
                    .fechaInicio(inicio)
                    .fechaFin(fin)
                    .activa(random.nextBoolean())
                    .tipo(tipos.get(random.nextInt(tipos.size())))
                    .build();

            repository.save(promocion);
        }
    }
}
