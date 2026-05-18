package com.prestamospro.reporteservice.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteMoraResponse {
    private Long clienteId;
    private String nombreCliente;
    private Long cobradorId;
    private Long prestamoId;
    private BigDecimal saldo;
    private Integer diasVencido;
}