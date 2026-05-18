package com.prestamospro.authservice;


import com.prestamospro.authservice.entity.Usuario;
import com.prestamospro.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
@RequiredArgsConstructor
@SpringBootApplication

public class AuthServiceApplication implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main (String [] args){
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Admin")
                    .email("Camiloandrescantero09@outlook.com")
                    .password(passwordEncoder.encode("Kiolopo09"))
                    .rol(Usuario.Rol.ADMIN)
                    .activo(true)
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .nombre("Antonio Cobrador")
                    .email("antonio@prestamospro.com")
                    .password(passwordEncoder.encode("AntonioCobrador"))
                    .rol(Usuario.Rol.COBRADOR)
                    .activo(true)
                    .build());

            System.out.println("✅ Usuarios iniciales creados");
        }
    }
}
