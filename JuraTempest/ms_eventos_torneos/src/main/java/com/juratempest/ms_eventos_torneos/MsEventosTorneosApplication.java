package com.juratempest.ms_eventos_torneos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsEventosTorneosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsEventosTorneosApplication.class, args);
    }
}
