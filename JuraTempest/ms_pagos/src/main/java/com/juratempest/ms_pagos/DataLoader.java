package com.juratempest.ms_pagos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.juratempest.ms_pagos.model.Pago;
import com.juratempest.ms_pagos.repository.PagoRepository;

import net.datafaker.Faker;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private static final int IVA_PORCENTAJE = 19;

    private final PagoRepository repository;

    public DataLoader(PagoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        Random random = new Random();
        List<String> metodos = List.of("EFECTIVO", "DEBITO", "CREDITO", "TRANSFERENCIA");
        List<String> estados = List.of("PENDIENTE", "APROBADO", "RECHAZADO", "ANULADO");

        for (int i = 0; i < 30; i++) {
            String estado = estados.get(random.nextInt(estados.size()));
            Integer valorNeto = faker.number().numberBetween(5000, 30000);
            Integer montoDescuento = random.nextBoolean() ? Math.round(valorNeto * faker.number().numberBetween(5, 31) / 100.0f) : 0;
            Integer baseConDescuento = valorNeto - montoDescuento;
            Integer iva = Math.round(baseConDescuento * IVA_PORCENTAJE / 100.0f);

            Pago pago = Pago.builder()
                    .usuarioId(faker.number().numberBetween(1L, 8L))
                    .reservaId((long) i + 1)
                    .promocionId(montoDescuento > 0 ? faker.number().numberBetween(1L, 5L) : null)
                    .valorNeto(valorNeto)
                    .montoDescuento(montoDescuento)
                    .iva(iva)
                    .montoFinal(baseConDescuento + iva)
                    .metodoPago(metodos.get(random.nextInt(metodos.size())))
                    .estado(estado)
                    .fechaCreacion(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30)))
                    .fechaPago("APROBADO".equals(estado) ? LocalDateTime.now().minusDays(faker.number().numberBetween(0, 15)) : null)
                    .build();

            repository.save(pago);
        }
    }
}
