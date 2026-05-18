package com.prestamospro.pagoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenDiaResponse {
    private LocalDate fecha;
    private Long cobradorId;
    private BigDecimal totalRecaudado;
    private Integer clientesPagaron;
    private Integer clientesNoPagaron;
    private List<PagoResponse> pagos;

}
