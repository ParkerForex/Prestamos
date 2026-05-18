package com.prestamospro.reporteservice.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteCobradorResponse {
    private Long cobradorId;
    private String nombreCobrador;
    private BigDecimal recaudadoHoy;
    private BigDecimal carteraRuta;
    private Integer clientesActivos;
    private Integer clientesEnMora;
}