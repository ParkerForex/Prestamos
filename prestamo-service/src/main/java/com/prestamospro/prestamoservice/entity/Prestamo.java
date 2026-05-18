package com.prestamospro.prestamoservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name="prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder



public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long cobradorId;

    @Column(nullable = false)
    private Long rutaId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cuotaDiaria;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Integer diasTotales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Estado estado = Estado.ACTIVO;

    public enum Estado{
        ACTIVO, VENCIDO, PAGADO
    }


}
