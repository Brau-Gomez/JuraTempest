package com.juratempest.ms_promociones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsPromocionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPromocionesApplication.class, args);
    }
}
