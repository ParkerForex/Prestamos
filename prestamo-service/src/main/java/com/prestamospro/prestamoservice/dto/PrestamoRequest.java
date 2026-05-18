package com.prestamospro.prestamoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoRequest {
    private Long clienteId;
    private Long cobradorId;
    private Long rutaId;
    private BigDecimal cuotaDiaria;
    private Integer dias;
    private LocalDate fechaInicio;
}