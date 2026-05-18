package com.prestamospro.clienteservice.service;

import com.prestamospro.clienteservice.dto.ClienteRequest;
import com.prestamospro.clienteservice.dto.ClienteResponse;
import com.prestamospro.clienteservice.entity.Cliente;
import com.prestamospro.clienteservice.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public ClienteResponse crear(ClienteRequest request) {
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .rutaId(request.getRutaId())
                .cobradorId(request.getCobradorId())
                .activo(true)
                .build();
        return toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse subirFoto(Long id, MultipartFile foto) throws IOException {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        String nombreArchivo = UUID.randomUUID() + "_" + foto.getOriginalFilename();
        Path destino = dir.resolve(nombreArchivo);
        Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        cliente.setFotoUrl(destino.toString());
        return toResponse(clienteRepository.save(cliente));
    }

    public List<ClienteResponse> listarPorCobrador(Long cobradorId) {
        return clienteRepository.findByCobradorId(cobradorId)
                .stream().map(this::toResponse).toList();
    }

    public List<ClienteResponse> listarPorRuta(Long rutaId) {
        return clienteRepository.findByRutaId(rutaId)
                .stream().map(this::toResponse).toList();
    }

    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findByActivoTrue()
                .stream().map(this::toResponse).toList();
    }

    public ClienteResponse obtener(Long id) {
        return toResponse(clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        cliente.setNombre(request.getNombre());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        return toResponse(clienteRepository.save(cliente));
    }

    private ClienteResponse toResponse(Cliente c) {
        boolean tieneFoto = c.getFotoUrl() != null && !c.getFotoUrl().isEmpty();
        boolean tieneDireccion = c.getDireccion() != null && !c.getDireccion().isEmpty();
        return ClienteResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .telefono(c.getTelefono())
                .direccion(c.getDireccion())
                .fotoUrl(c.getFotoUrl())
                .rutaId(c.getRutaId())
                .cobradorId(c.getCobradorId())
                .activo(c.getActivo())
                .tieneFoto(tieneFoto)
                .tieneDireccion(tieneDireccion)
                .puedeCrearPrestamo(tieneFoto && tieneDireccion)
                .build();
    }
}