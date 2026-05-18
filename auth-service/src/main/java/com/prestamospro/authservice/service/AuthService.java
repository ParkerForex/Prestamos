package com.prestamospro.authservice.service;

import com.prestamospro.authservice.dtos.*;
import com.prestamospro.authservice.entity.Usuario;
import com.prestamospro.authservice.repository.UsuarioRepository;
import com.prestamospro.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getNombre()
        );

        return new LoginResponse(
                token,
                usuario.getNombre(),
                usuario.getRol().name(),
                usuario.getEmail()
        );
    }
}
