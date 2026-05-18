package com.prestamospro.clienteservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClienteRequest {

    private String nombre;
    private String telefono;
    private String direccion;
    private Long rutaId;
    private Long cobradorId;

}
