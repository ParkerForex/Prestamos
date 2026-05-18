package com.prestamospro.prestamoservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PrestamoKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void prestamoCreado(Long prestamoId, Long clienteId,
                               Long cobradorId, Object monto) {
        kafkaTemplate.send("prestamo.creado", Map.of(
                "prestamoId", prestamoId,
                "clienteId", clienteId,
                "cobradorId", cobradorId,
                "monto", monto
        ));
        System.out.println("📤 Evento prestamo.creado enviado: " + prestamoId);
    }

    public void prestamoEnMora(Long prestamoId, Long clienteId,
                               Long cobradorId, Integer diasVencido) {
        kafkaTemplate.send("prestamo.en-mora", Map.of(
                "prestamoId", prestamoId,
                "clienteId", clienteId,
                "cobradorId", cobradorId,
                "diasVencido", diasVencido
        ));
        System.out.println("📤 Evento prestamo.en-mora enviado: " + prestamoId);
    }
}