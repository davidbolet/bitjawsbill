package com.bitjawsbill.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuentas_bancarias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@ManyToOne
	@JoinColumn(name = "organizacion_id")
	private Organizacion organizacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private String entidad;
    private String iban;
    private String swiftBic;
    private Boolean cuentaPredeterminada;

    // Getters y Setters
}