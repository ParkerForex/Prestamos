package com.prestamospro.clienteservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String direccion;

    @Column
    private String fotoUrl;

    @Column(nullable = false)
    private Long rutaId;

    @Column(nullable = false)
    private Long cobradorId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;


}
