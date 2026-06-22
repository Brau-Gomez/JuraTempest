package com.juratempest.ms_promociones;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class MsPromocionesApplicationTests {

    @Test
    void applicationSePuedeInstanciar() {
        assertDoesNotThrow(MsPromocionesApplication::new);
    }
}
