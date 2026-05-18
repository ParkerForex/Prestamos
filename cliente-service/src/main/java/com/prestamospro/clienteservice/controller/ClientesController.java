package com.prestamospro.clienteservice.controller;

import com.prestamospro.clienteservice.dto.ClienteRequest;
import com.prestamospro.clienteservice.dto.ClienteResponse;
import com.prestamospro.clienteservice.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClientesController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.crear(request));
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<ClienteResponse> subirFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto) throws IOException {
        return ResponseEntity.ok(clienteService.subirFoto(id, foto));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtener(id));
    }

    @GetMapping("/cobrador/{cobradorId}")
    public ResponseEntity<List<ClienteResponse>> porCobrador(@PathVariable Long cobradorId) {
        return ResponseEntity.ok(clienteService.listarPorCobrador(cobradorId));
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<ClienteResponse>> porRuta(@PathVariable Long rutaId) {
        return ResponseEntity.ok(clienteService.listarPorRuta(rutaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }
}