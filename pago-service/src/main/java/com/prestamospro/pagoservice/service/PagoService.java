package com.prestamospro.pagoservice.service;
import com.prestamospro.pagoservice.kafka.PagoKafkaProducer;
import com.prestamospro.pagoservice.dto.*;
import com.prestamospro.pagoservice.entity.Pago;
import com.prestamospro.pagoservice.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final RestTemplate restTemplate;
    private final PagoKafkaProducer kafkaProducer;
    @Value("${prestamo.service.url}")
    private String prestamoServiceUrl;

    public PagoResponse registrarPago(PagoRequest request, String token) {

        // 1. Obtener el préstamo del prestamo-service con token
        String url = prestamoServiceUrl + "/api/prestamos/" + request.getPrestamoId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);

        Map prestamo = response.getBody();
        if (prestamo == null) {
            throw new RuntimeException("Préstamo no encontrado");
        }

        String estado = (String) prestamo.get("estado");
        if (!"ACTIVO".equals(estado)) {
            throw new RuntimeException("El préstamo no está activo");
        }

        // 2. Registrar el pago
        Pago pago = Pago.builder()
                .prestamoId(request.getPrestamoId())
                .clienteId(request.getClienteId())
                .cobradorId(request.getCobradorId())
                .montoPagado(request.getMontoPagado())
                .fechaPago(LocalDate.now())
                .observacion(request.getObservacion())
                .build();

        pagoRepository.save(pago);

        // 3. Calcular nuevo saldo
        BigDecimal saldoActual = new BigDecimal(prestamo.get("saldo").toString());
        BigDecimal nuevoSaldo = saldoActual.subtract(request.getMontoPagado());
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            nuevoSaldo = BigDecimal.ZERO;
        }

        String estadoNuevo = nuevoSaldo.compareTo(BigDecimal.ZERO) == 0 ? "PAGADO" : "ACTIVO";

        // 4. Actualizar saldo en prestamo-service con token
        String urlActualizar = prestamoServiceUrl + "/api/prestamos/" +
                request.getPrestamoId() + "/pago";

        Map<String, Object> body = Map.of(
                "montoPagado", request.getMontoPagado(),
                "saldoNuevo", nuevoSaldo,
                "estado", estadoNuevo
        );

        HttpHeaders headersUpdate = new HttpHeaders();
        headersUpdate.set("Authorization", token);
        headersUpdate.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entityUpdate =
                new HttpEntity<>(body, headersUpdate);

        restTemplate.exchange(urlActualizar, HttpMethod.PUT,
                entityUpdate, Map.class);

        // Publicar evento pago.recibido
        kafkaProducer.pagoRecibido(
                pago.getId(),
                pago.getPrestamoId(),
                pago.getClienteId(),
                pago.getCobradorId(),
                pago.getMontoPagado()
        );
        // Si el préstamo quedó pagado publicar evento
        if ("PAGADO".equals(estadoNuevo)) {
            kafkaProducer.prestamoPagado(
                    pago.getPrestamoId(),
                    pago.getClienteId(),
                    pago.getCobradorId()
            );
        }

        return PagoResponse.builder()
                .id(pago.getId())
                .prestamoId(pago.getPrestamoId())
                .clienteId(pago.getClienteId())
                .cobradorId(pago.getCobradorId())
                .montoPagado(pago.getMontoPagado())
                .fechaPago(pago.getFechaPago())
                .observacion(pago.getObservacion())
                .saldoActual(nuevoSaldo)
                .estadoPrestamo(estadoNuevo)
                .build();
    }

    public ResumenDiaResponse resumenDelDia(Long cobradorId) {
        LocalDate hoy = LocalDate.now();
        List<Pago> pagos = pagoRepository
                .findByCobradorIdAndFechaPago(cobradorId, hoy);

        BigDecimal total = pagoRepository
                .totalRecaudadoPorCobradorYFecha(cobradorId, hoy);
        if (total == null) total = BigDecimal.ZERO;

        long pagaron = pagos.stream()
                .filter(p -> p.getMontoPagado().compareTo(BigDecimal.ZERO) > 0)
                .count();

        long noPagaron = pagos.stream()
                .filter(p -> p.getMontoPagado().compareTo(BigDecimal.ZERO) == 0)
                .count();

        List<PagoResponse> pagoResponses = pagos.stream()
                .map(this::toResponse).toList();

        return ResumenDiaResponse.builder()
                .fecha(hoy)
                .cobradorId(cobradorId)
                .totalRecaudado(total)
                .clientesPagaron((int) pagaron)
                .clientesNoPagaron((int) noPagaron)
                .pagos(pagoResponses)
                .build();
    }

    public List<PagoResponse> historialPrestamo(Long prestamoId) {
        return pagoRepository.findByPrestamoId(prestamoId)
                .stream().map(this::toResponse).toList();
    }

    public BigDecimal totalDelDia(Long cobradorId) {
        BigDecimal total = pagoRepository.totalRecaudadoPorCobradorYFecha(
                cobradorId, LocalDate.now());
        return total != null ? total : BigDecimal.ZERO;
    }

    private PagoResponse toResponse(Pago p) {
        return PagoResponse.builder()
                .id(p.getId())
                .prestamoId(p.getPrestamoId())
                .clienteId(p.getClienteId())
                .cobradorId(p.getCobradorId())
                .montoPagado(p.getMontoPagado())
                .fechaPago(p.getFechaPago())
                .observacion(p.getObservacion())
                .build();
    }
}