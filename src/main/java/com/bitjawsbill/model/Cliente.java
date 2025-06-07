package com.bitjawsbill.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "organizacion_id")
	@JsonIgnore
	private Organizacion organizacion;

	@Enumerated(EnumType.ORDINAL)
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
	
	// Datos adicionales
	private String observaciones;
	private Boolean activo = true;

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<CuentaBancaria> cuentasBancarias = new ArrayList<>();

	@OneToMany(mappedBy = "cliente")
	@JsonIgnore
	private List<Factura> facturas = new ArrayList<>();
}