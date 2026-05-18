package com.prestamospro.prestamoservice.controller;

import com.prestamospro.prestamoservice.dto.PrestamoRequest;
import com.prestamospro.prestamoservice.dto.PrestamoResponse;
import com.prestamospro.prestamoservice.service.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponse> crear(
            @RequestBody PrestamoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(prestamoService.crear(request, token));
    }

    @PostMapping("/renovar/{clienteId}")
    public ResponseEntity<PrestamoResponse> renovar(
            @PathVariable Long clienteId,
            @RequestBody PrestamoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(prestamoService.renovar(clienteId, request, token));
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponse>> listarActivos() {
        return ResponseEntity.ok(prestamoService.listarActivos());
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<PrestamoResponse>> listarVencidos() {
        return ResponseEntity.ok(prestamoService.listarVencidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.obtener(id));
    }

    @GetMapping("/cobrador/{cobradorId}")
    public ResponseEntity<List<PrestamoResponse>> porCobrador(@PathVariable Long cobradorId) {
        return ResponseEntity.ok(prestamoService.listarPorCobrador(cobradorId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PrestamoResponse>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(prestamoService.listarPorCliente(clienteId));
    }

    @PutMapping("/{id}/pago")
    public ResponseEntity<PrestamoResponse> actualizarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(prestamoService.actualizarSaldo(id, body));
    }
}