package com.prestamospro.pagoservice.repository;

import com.prestamospro.pagoservice.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long>{
    List<Pago> findByPrestamoId(Long prestamoID);
    List<Pago> findByCobradorIdAndFechaPago(Long cobradorId, LocalDate fecha);
    List<Pago> findByFechaPago(LocalDate fecha);

    @Query("SELECT COALESCE(SUM(p.montoPagado), 0) FROM Pago p WHERE p.cobradorId = :cobradorId AND p.fechaPago = :fecha")
    BigDecimal totalRecaudadoPorCobradorYFecha(
            @Param("cobradorId") Long cobradorId,
            @Param("fecha") LocalDate fecha
    );

    @Query("SELECT COALESCE(SUM(p.montoPagado), 0) FROM Pago p WHERE p.fechaPago = :fecha")
    BigDecimal totalRecaudoPorFecha(@Param("fecha") LocalDate fecha);
}