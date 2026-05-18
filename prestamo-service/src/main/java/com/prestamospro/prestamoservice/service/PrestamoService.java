package com.prestamospro.prestamoservice.service;

import com.prestamospro.prestamoservice.dto.PrestamoRequest;
import com.prestamospro.prestamoservice.dto.PrestamoResponse;
import com.prestamospro.prestamoservice.entity.Prestamo;
import com.prestamospro.prestamoservice.kafka.PrestamoKafkaProducer;
import com.prestamospro.prestamoservice.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final RestTemplate restTemplate;
    private final PrestamoKafkaProducer kafkaProducer;

    @Value("${cliente.service.url}")
    private String clienteServiceUrl;

    public PrestamoResponse crear(PrestamoRequest request, String token) {

        // 1. Verificar cliente con token
        String url = clienteServiceUrl + "/api/clientes/" + request.getClienteId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);

        Map cliente = response.getBody();
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        Boolean puedeCrearPrestamo = (Boolean) cliente.get("puedeCrearPrestamo");
        if (!puedeCrearPrestamo) {
            throw new RuntimeException("El cliente no tiene foto o dirección registrada");
        }

        // 2. Verificar que no tenga préstamo activo
        prestamoRepository.findByClienteIdAndEstado(
                        request.getClienteId(), Prestamo.Estado.ACTIVO)
                .ifPresent(p -> {
                    throw new RuntimeException("El cliente ya tiene un préstamo activo");
                });

        // 3. Calcular total y fecha fin saltando domingos
        BigDecimal monto = request.getCuotaDiaria()
                .multiply(BigDecimal.valueOf(request.getDias()));

        LocalDate fechaFin = calcularFechaFin(request.getFechaInicio(), request.getDias());

        // 4. Crear y guardar el préstamo
        Prestamo prestamo = Prestamo.builder()
                .clienteId(request.getClienteId())
                .cobradorId(request.getCobradorId())
                .rutaId(request.getRutaId())
                .monto(monto)
                .cuotaDiaria(request.getCuotaDiaria())
                .saldo(monto)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(fechaFin)
                .diasTotales(request.getDias())
                .estado(Prestamo.Estado.ACTIVO)
                .build();

        Prestamo saved = prestamoRepository.save(prestamo);

        // 5. Publicar evento Kafka
        kafkaProducer.prestamoCreado(
                saved.getId(),
                saved.getClienteId(),
                saved.getCobradorId(),
                saved.getMonto()
        );

        return toResponse(saved);
    }

    public PrestamoResponse renovar(Long clienteId, PrestamoRequest request, String token) {
        Prestamo actual = prestamoRepository
                .findByClienteIdAndEstado(clienteId, Prestamo.Estado.ACTIVO)
                .orElseThrow(() -> new RuntimeException("No hay préstamo activo para renovar"));

        actual.setEstado(Prestamo.Estado.PAGADO);
        prestamoRepository.save(actual);

        request.setClienteId(clienteId);
        return crear(request, token);
    }

    public List<PrestamoResponse> listarPorCobrador(Long cobradorId) {
        return prestamoRepository.findByCobradorId(cobradorId)
                .stream().map(this::toResponse).toList();
    }

    public List<PrestamoResponse> listarPorCliente(Long clienteId) {
        return prestamoRepository.findByClienteId(clienteId)
                .stream().map(this::toResponse).toList();
    }

    public List<PrestamoResponse> listarActivos() {
        return prestamoRepository.findByEstado(Prestamo.Estado.ACTIVO)
                .stream().map(this::toResponse).toList();
    }

    public List<PrestamoResponse> listarVencidos() {
        return prestamoRepository.findByEstado(Prestamo.Estado.VENCIDO)
                .stream().map(this::toResponse).toList();
    }

    public PrestamoResponse obtener(Long id) {
        return toResponse(prestamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado")));
    }

    public PrestamoResponse actualizarSaldo(Long id, Map<String, Object> body) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        Double saldoNuevo = ((Number) body.get("saldoNuevo")).doubleValue();
        String estado = (String) body.get("estado");

        prestamo.setSaldo(BigDecimal.valueOf(saldoNuevo));
        prestamo.setEstado(Prestamo.Estado.valueOf(estado));

        // Si pasa a VENCIDO publicar evento Kafka
        if ("VENCIDO".equals(estado)) {
            kafkaProducer.prestamoEnMora(
                    prestamo.getId(),
                    prestamo.getClienteId(),
                    prestamo.getCobradorId(),
                    0
            );
        }

        return toResponse(prestamoRepository.save(prestamo));
    }

    private LocalDate calcularFechaFin(LocalDate inicio, int dias) {
        LocalDate fecha = inicio;
        int diasContados = 0;
        while (diasContados < dias) {
            if (fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
                diasContados++;
            }
            if (diasContados < dias) {
                fecha = fecha.plusDays(1);
            }
        }
        return fecha;
    }

    private PrestamoResponse toResponse(Prestamo p) {
        int diasRestantes = calcularDiasHabilesRestantes(p.getFechaFin());
        return PrestamoResponse.builder()
                .id(p.getId())
                .clienteId(p.getClienteId())
                .cobradorId(p.getCobradorId())
                .rutaId(p.getRutaId())
                .monto(p.getMonto())
                .cuotaDiaria(p.getCuotaDiaria())
                .saldo(p.getSaldo())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .diasTotales(p.getDiasTotales())
                .diasRestantes(diasRestantes)
                .estado(p.getEstado().name())
                .build();
    }

    private int calcularDiasHabilesRestantes(LocalDate fechaFin) {
        LocalDate hoy = LocalDate.now();
        if (hoy.isAfter(fechaFin)) return 0;
        int dias = 0;
        LocalDate fecha = hoy;
        while (!fecha.isAfter(fechaFin)) {
            if (fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dias++;
            }
            fecha = fecha.plusDays(1);
        }
        return dias;
    }
}