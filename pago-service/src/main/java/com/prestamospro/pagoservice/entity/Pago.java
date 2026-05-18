package com.prestamospro.pagoservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name= "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long prestamoId;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long cobradorId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montoPagado;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Column
    private String observacion;
}
