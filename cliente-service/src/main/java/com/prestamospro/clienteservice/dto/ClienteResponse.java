package com.prestamospro.clienteservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String fotoUrl;
    private Long rutaId;
    private Long cobradorId;
    private boolean activo;
    private boolean tieneFoto;
    private boolean tieneDireccion;
    private boolean puedeCrearPrestamo;
}
