package com.bitjawsbill.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.model.dto.FacturaDTO;
import com.bitjawsbill.model.dto.FacturaResponseDTO;
import com.bitjawsbill.service.FacturaService;
import com.bitjawsbill.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;
    private final UsuarioService usuarioService; // Obtener usuario autenticado

    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crearFactura(@RequestBody FacturaDTO facturaDTO, Principal principal) {
        try {
            Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
            Factura facturaCreada = facturaService.crearFactura(facturaDTO, usuario);
            return ResponseEntity.created(URI.create("/api/facturas/" + facturaCreada.getId()))
                .body(FacturaResponseDTO.fromEntity(facturaCreada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Factura>> listarFacturas(Principal principal) {
        Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        List<Factura> facturas = facturaService.listarFacturasPorOrganizacion(usuario.getOrganizacion());
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerFactura(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        try {
            Factura factura = facturaService.obtenerFacturaPorIdYOrganizacion(id, usuario.getOrganizacion());
            return ResponseEntity.ok(FacturaResponseDTO.fromEntity(factura));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
