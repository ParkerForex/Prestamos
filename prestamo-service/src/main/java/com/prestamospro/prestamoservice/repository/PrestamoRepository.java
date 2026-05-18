package com.prestamospro.prestamoservice.repository;

import com.prestamospro.prestamoservice.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long>{
    List<Prestamo> findByCobradorId(Long cobradorId);
    List<Prestamo> findByRutaId(Long rutaId);
    List<Prestamo> findByClienteId(Long clienteId);
    Optional<Prestamo> findByClienteIdAndEstado(Long clienteId, Prestamo.Estado estado);
    List<Prestamo> findByEstado(Prestamo.Estado estado);

}