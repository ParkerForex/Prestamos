package com.prestamospro.reporteservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteGlobalResponse {
    private LocalDate fecha;
    private BigDecimal carteraTotal;
    private BigDecimal recaudadoHoy;
    private Integer prestamosActivos;
    private Integer prestamosVencidos;
    private Integer prestamosPagados;
    private List<ReporteCobradorResponse> porCobrador;
    private List<ClienteMoraResponse> clientesEnMora;
}