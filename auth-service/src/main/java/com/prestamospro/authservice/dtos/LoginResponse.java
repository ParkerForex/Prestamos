package com.prestamospro.authservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
     private String token;
     private String nombre;
     private String rol;
     private String email;
}