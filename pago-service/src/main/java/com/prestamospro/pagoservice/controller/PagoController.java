package com.prestamospro.pagoservice.controller;

import com.prestamospro.pagoservice.dto.*;
import com.prestamospro.pagoservice.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponse> registrar(
            @RequestBody PagoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(pagoService.registrarPago(request, token));
    }

    @GetMapping("/resumen/{cobradorId}")
    public ResponseEntity<ResumenDiaResponse> resumenDia(
            @PathVariable Long cobradorId) {
        return ResponseEntity.ok(pagoService.resumenDelDia(cobradorId));
    }

    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<List<PagoResponse>> historial(
            @PathVariable Long prestamoId) {
        return ResponseEntity.ok(pagoService.historialPrestamo(prestamoId));
    }

    @GetMapping("/total/{cobradorId}")
    public ResponseEntity<BigDecimal> totalDia(
            @PathVariable Long cobradorId) {
        return ResponseEntity.ok(pagoService.totalDelDia(cobradorId));
    }
}