package com.bitjawsbill.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facturas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organizacion_id", "serie", "numero_factura"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFactura;
    private String serie;
    private LocalDate fechaEmision;

	@ManyToOne
	@JoinColumn(name = "organizacion_id")
	@JsonIgnore
	private Organizacion organizacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnore
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "emisor_id")
    @JsonIgnore
    private Cliente emisor;

    @Enumerated(EnumType.STRING)
    private TipoFactura tipoFactura;

    private String descripcion;
    private BigDecimal totalBase;
    private BigDecimal totalImpuestos;
    private BigDecimal totalFactura;
    private String moneda;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    private Boolean esRecibida;

    @ManyToOne
    @JoinColumn(name = "factura_rectificada_id")
	@JsonIgnore
    private Factura facturaRectificada;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

	@OneToMany(mappedBy = "factura")
	@JsonManagedReference
	private List<FacturaLinea> lineas = new ArrayList<>();

    // Getters, Setters
}