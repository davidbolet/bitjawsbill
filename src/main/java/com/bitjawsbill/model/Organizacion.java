package com.bitjawsbill.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;

@Entity
@Table(name = "organizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;               // Ej: "Contabilidad Pedro", "Acme S.L."
    private TipoCliente tipoCliente;                // "AUTONOMO" o "EMPRESA"
    private String nif;
    private String emailContacto;
	private String telefono;
	private String direccion;
	private String ciudad;
	private String pais;

	@OneToOne(mappedBy = "organizacion")
	private Cliente cliente;

    @OneToMany(mappedBy = "organizacion", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Usuario> usuarios = new ArrayList<>();

    // Getters y Setters
}
