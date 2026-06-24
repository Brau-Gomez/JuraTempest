package com.juratempest.ms_mantenimiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsMantenimientoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsMantenimientoApplication.class, args);
    }
}
