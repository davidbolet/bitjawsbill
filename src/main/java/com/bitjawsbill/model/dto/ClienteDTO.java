package com.bitjawsbill.model.dto;

import com.bitjawsbill.model.TipoCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private Long organizacionId;
    private TipoCliente tipoCliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    // Dirección
    private String direccion;
    private String numero;
    private String piso;
    private String puerta;
    private String codigoPostal;
    private String ciudad;
    private String provincia;
    private String pais;
    
    // Datos fiscales
    private String nif;
    private String cif;
    private String razonSocial;
    private String nombreComercial;
    
    // Datos de contacto adicionales
    private String personaContacto;
    private String cargoContacto;
    private String telefonoContacto;
    private String emailContacto;
    
    // Datos bancarios
    private List<CuentaBancariaDTO> cuentasBancarias = new ArrayList<>();
    
    // Datos adicionales
    private String observaciones;
    private Boolean activo = true;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaBancariaDTO {
        private Long id;
        private String iban;
        private String swift;
        private String banco;
        private String titular;
        private String observaciones;
    }
} 