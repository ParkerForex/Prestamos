package com.prestamospro.reporteservice.service;

import com.prestamospro.reporteservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${prestamo.service.url}")
    private String prestamoServiceUrl;

    @Value("${pago.service.url}")
    private String pagoServiceUrl;

    @Value("${cliente.service.url}")
    private String clienteServiceUrl;

    private HttpEntity<String> entityConToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return new HttpEntity<>(headers);
    }

    public ReporteGlobalResponse reporteGlobal(String token) {

        // 1. Obtener todos los préstamos activos
        ResponseEntity<List> activos = restTemplate.exchange(
                prestamoServiceUrl + "/api/prestamos",
                HttpMethod.GET, entityConToken(token), List.class);

        // 2. Obtener préstamos vencidos
        ResponseEntity<List> vencidos = restTemplate.exchange(
                prestamoServiceUrl + "/api/prestamos/vencidos",
                HttpMethod.GET, entityConToken(token), List.class);

        List<Map> prestamosActivos = activos.getBody() != null ?
                activos.getBody() : new ArrayList<>();
        List<Map> prestamosVencidos = vencidos.getBody() != null ?
                vencidos.getBody() : new ArrayList<>();

        // 3. Calcular cartera total
        BigDecimal carteraTotal = prestamosActivos.stream()
                .map(p -> new BigDecimal(p.get("saldo").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Clientes en mora
        List<ClienteMoraResponse> mora = new ArrayList<>();
        for (Map p : prestamosVencidos) {
            Long clienteId = Long.valueOf(p.get("clienteId").toString());
            Long prestamoId = Long.valueOf(p.get("id").toString());
            Long cobradorId = Long.valueOf(p.get("cobradorId").toString());
            BigDecimal saldo = new BigDecimal(p.get("saldo").toString());
            Integer diasRestantes = (Integer) p.get("diasRestantes");

            try {
                ResponseEntity<Map> clienteResp = restTemplate.exchange(
                        clienteServiceUrl + "/api/clientes/" + clienteId,
                        HttpMethod.GET, entityConToken(token), Map.class);
                String nombre = clienteResp.getBody() != null ?
                        (String) clienteResp.getBody().get("nombre") : "Desconocido";

                mora.add(ClienteMoraResponse.builder()
                        .clienteId(clienteId)
                        .nombreCliente(nombre)
                        .cobradorId(cobradorId)
                        .prestamoId(prestamoId)
                        .saldo(saldo)
                        .diasVencido(Math.abs(diasRestantes != null ? diasRestantes : 0))
                        .build());
            } catch (Exception e) {
                mora.add(ClienteMoraResponse.builder()
                        .clienteId(clienteId)
                        .cobradorId(cobradorId)
                        .prestamoId(prestamoId)
                        .saldo(saldo)
                        .diasVencido(Math.abs(diasRestantes != null ? diasRestantes : 0))
                        .build());
            }
        }

        // 5. Recaudo total del día
        BigDecimal recaudadoHoy = BigDecimal.ZERO;
        try {
            List<Long> cobradores = prestamosActivos.stream()
                    .map(p -> Long.valueOf(p.get("cobradorId").toString()))
                    .distinct().toList();

            for (Long cobradorId : cobradores) {
                ResponseEntity<BigDecimal> totalResp = restTemplate.exchange(
                        pagoServiceUrl + "/api/pagos/total/" + cobradorId,
                        HttpMethod.GET, entityConToken(token), BigDecimal.class);
                if (totalResp.getBody() != null) {
                    recaudadoHoy = recaudadoHoy.add(totalResp.getBody());
                }
            }
        } catch (Exception e) {
            recaudadoHoy = BigDecimal.ZERO;
        }

        // 6. Recaudo por cobrador
        List<ReporteCobradorResponse> porCobrador = new ArrayList<>();
        try {
            List<Long> cobradores = prestamosActivos.stream()
                    .map(p -> Long.valueOf(p.get("cobradorId").toString()))
                    .distinct().toList();

            for (Long cobradorId : cobradores) {
                porCobrador.add(reporteCobrador(cobradorId, token));
            }
        } catch (Exception e) {
            // si falla no rompe el reporte global
        }

        return ReporteGlobalResponse.builder()
                .fecha(LocalDate.now())
                .carteraTotal(carteraTotal)
                .recaudadoHoy(recaudadoHoy)
                .prestamosActivos(prestamosActivos.size())
                .prestamosVencidos(prestamosVencidos.size())
                .prestamosPagados(0)
                .porCobrador(porCobrador)
                .clientesEnMora(mora)
                .build();
    }

    public ReporteCobradorResponse reporteCobrador(Long cobradorId, String token) {

        // 1. Préstamos del cobrador
        ResponseEntity<List> prestamosResp = restTemplate.exchange(
                prestamoServiceUrl + "/api/prestamos/cobrador/" + cobradorId,
                HttpMethod.GET, entityConToken(token), List.class);

        List<Map> prestamos = prestamosResp.getBody() != null ?
                prestamosResp.getBody() : new ArrayList<>();

        // 2. Cartera del cobrador
        BigDecimal cartera = prestamos.stream()
                .filter(p -> "ACTIVO".equals(p.get("estado")))
                .map(p -> new BigDecimal(p.get("saldo").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Activos y en mora
        long activos = prestamos.stream()
                .filter(p -> "ACTIVO".equals(p.get("estado"))).count();
        long mora = prestamos.stream()
                .filter(p -> "VENCIDO".equals(p.get("estado"))).count();

        // 4. Total recaudado hoy
        BigDecimal recaudadoHoy = BigDecimal.ZERO;
        try {
            ResponseEntity<BigDecimal> totalResp = restTemplate.exchange(
                    pagoServiceUrl + "/api/pagos/total/" + cobradorId,
                    HttpMethod.GET, entityConToken(token), BigDecimal.class);
            if (totalResp.getBody() != null) {
                recaudadoHoy = totalResp.getBody();
            }
        } catch (Exception e) {
            recaudadoHoy = BigDecimal.ZERO;
        }

        // 5. Nombre del cobrador
        String nombreCobrador = "Cobrador " + cobradorId;
        try {
            ResponseEntity<Map> cobradorResp = restTemplate.exchange(
                    authServiceUrl + "/api/auth/usuarios/" + cobradorId,
                    HttpMethod.GET, entityConToken(token), Map.class);
            if (cobradorResp.getBody() != null) {
                nombreCobrador = (String) cobradorResp.getBody().get("nombre");
            }
        } catch (Exception e) {
            nombreCobrador = "Cobrador " + cobradorId;
        }

        return ReporteCobradorResponse.builder()
                .cobradorId(cobradorId)
                .nombreCobrador(nombreCobrador)
                .recaudadoHoy(recaudadoHoy)
                .carteraRuta(cartera)
                .clientesActivos((int) activos)
                .clientesEnMora((int) mora)
                .build();
    }
}