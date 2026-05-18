package com.prestamospro.authservice.controller;
import com.prestamospro.authservice.repository.UsuarioRepository;
import com.prestamospro.authservice.dtos.LoginRequest;
import com.prestamospro.authservice.dtos.LoginResponse;
import com.prestamospro.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println(">>> LOGIN REQUEST: " + request.getEmail());
        try {
            LoginResponse response = authService.login(request);
            System.out.println(">>> LOGIN OK: " + response.getToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(">>> LOGIN ERROR: " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(Map.of(
                        "id", u.getId(),
                        "nombre", u.getNombre(),
                        "email", u.getEmail(),
                        "rol", u.getRol().name()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}