package com.bitjawsbill.controller;

import com.bitjawsbill.model.dto.ClienteDTO;
import com.bitjawsbill.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "API para la gestión de clientes")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Obtener todos los clientes", description = "Retorna una lista de todos los clientes de la organización del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> getAllClientes(Principal principal) {
        return ResponseEntity.ok(clienteService.findAll(principal));
    }

    @Operation(summary = "Obtener un cliente por ID", description = "Retorna un cliente específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> getClienteById(
            @Parameter(description = "ID del cliente a buscar") @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(clienteService.findById(id, principal));
    }

    @Operation(summary = "Crear un nuevo cliente", description = "Crea un nuevo cliente en la organización del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente creado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de cliente inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> createCliente(
            @Parameter(description = "Datos del cliente a crear") @RequestBody ClienteDTO clienteDTO,
            Principal principal) {
        return ResponseEntity.ok(clienteService.save(clienteDTO, principal));
    }

    @Operation(summary = "Actualizar un cliente existente", description = "Actualiza los datos de un cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ClienteDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de cliente inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> updateCliente(
            @Parameter(description = "ID del cliente a actualizar") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del cliente") @RequestBody ClienteDTO clienteDTO,
            Principal principal) {
        clienteDTO.setId(id);
        return ResponseEntity.ok(clienteService.update(clienteDTO, principal));
    }

    @Operation(summary = "Eliminar un cliente", description = "Elimina un cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar el cliente porque tiene facturas asociadas"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(
            @Parameter(description = "ID del cliente a eliminar") @PathVariable Long id,
            Principal principal) {
        clienteService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }
} 