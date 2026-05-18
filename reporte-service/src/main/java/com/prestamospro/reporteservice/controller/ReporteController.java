package com.prestamospro.reporteservice.controller;

import com.prestamospro.reporteservice.dto.*;
import com.prestamospro.reporteservice.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/global")
    public ResponseEntity<ReporteGlobalResponse> global(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(reporteService.reporteGlobal(token));
    }

    @GetMapping("/cobrador/{cobradorId}")
    public ResponseEntity<ReporteCobradorResponse> cobrador(
            @PathVariable Long cobradorId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(reporteService.reporteCobrador(cobradorId, token));
    }
}