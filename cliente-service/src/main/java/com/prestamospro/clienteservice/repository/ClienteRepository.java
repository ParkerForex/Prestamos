package com.prestamospro.clienteservice.repository;

import com.prestamospro.clienteservice.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByRutaId(Long rutaId);
    List<Cliente> findByCobradorId(Long cobradorId);
    List<Cliente> findByActivoTrue();
}