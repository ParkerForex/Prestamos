package com.prestamospro.pagoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.prestamospro.pagoservice")
public class PagoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PagoServiceApplication.class, args);
    }
}