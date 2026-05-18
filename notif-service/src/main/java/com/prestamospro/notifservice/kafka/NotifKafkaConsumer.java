package com.prestamospro.notifservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotifKafkaConsumer {

    @KafkaListener(topics = "prestamo.creado", groupId = "notif-group")
    public void onPrestamoCreado(Map<String, Object> evento) {
        System.out.println("✅ NUEVO PRÉSTAMO CREADO:");
        System.out.println("   → PrestamoId: " + evento.get("prestamoId"));
        System.out.println("   → ClienteId: " + evento.get("clienteId"));
        System.out.println("   → CobradorId: " + evento.get("cobradorId"));
        System.out.println("   → Monto: $" + evento.get("monto"));
    }

    @KafkaListener(topics = "pago.recibido", groupId = "notif-group")
    public void onPagoRecibido(Map<String, Object> evento) {
        System.out.println("💰 PAGO RECIBIDO:");
        System.out.println("   → PagoId: " + evento.get("pagoId"));
        System.out.println("   → PrestamoId: " + evento.get("prestamoId"));
        System.out.println("   → ClienteId: " + evento.get("clienteId"));
        System.out.println("   → Monto: $" + evento.get("monto"));
    }

    @KafkaListener(topics = "prestamo.en-mora", groupId = "notif-group")
    public void onPrestamoEnMora(Map<String, Object> evento) {
        System.out.println("⚠️ PRÉSTAMO EN MORA:");
        System.out.println("   → PrestamoId: " + evento.get("prestamoId"));
        System.out.println("   → ClienteId: " + evento.get("clienteId"));
        System.out.println("   → CobradorId: " + evento.get("cobradorId"));
        System.out.println("   → Días vencido: " + evento.get("diasVencido"));
    }

    @KafkaListener(topics = "prestamo.pagado", groupId = "notif-group")
    public void onPrestamoPagado(Map<String, Object> evento) {
        System.out.println("🎉 PRÉSTAMO TOTALMENTE PAGADO:");
        System.out.println("   → PrestamoId: " + evento.get("prestamoId"));
        System.out.println("   → ClienteId: " + evento.get("clienteId"));
        System.out.println("   → CobradorId: " + evento.get("cobradorId"));
    }
}