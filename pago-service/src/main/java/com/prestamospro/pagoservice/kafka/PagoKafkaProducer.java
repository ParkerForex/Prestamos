package com.prestamospro.pagoservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PagoKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void pagoRecibido(Long pagoId, Long prestamoId,
                             Long clienteId, Long cobradorId, Object monto) {
        kafkaTemplate.send("pago.recibido", Map.of(
                "pagoId", pagoId,
                "prestamoId", prestamoId,
                "clienteId", clienteId,
                "cobradorId", cobradorId,
                "monto", monto
        ));
        System.out.println("📤 Evento pago.recibido enviado: " + pagoId);
    }

    public void prestamoPagado(Long prestamoId, Long clienteId,
                               Long cobradorId) {
        kafkaTemplate.send("prestamo.pagado", Map.of(
                "prestamoId", prestamoId,
                "clienteId", clienteId,
                "cobradorId", cobradorId
        ));
        System.out.println("📤 Evento prestamo.pagado enviado: " + prestamoId);
    }
}