package com.prestamospro.pagoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoResponse {
    private Long id;
    private Long prestamoId;
    private Long clienteId;
    private Long cobradorId;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private String observacion;
    private BigDecimal saldoActual;
    private String estadoPrestamo;

}
