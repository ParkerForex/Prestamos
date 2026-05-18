package com.prestamospro.prestamoservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoResponse {
    private Long id;
    private Long clienteId;
    private Long cobradorId;
    private Long rutaId;
    private BigDecimal monto;
    private BigDecimal cuotaDiaria;
    private BigDecimal saldo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasTotales;
    private Integer diasRestantes;
    private String estado;
}