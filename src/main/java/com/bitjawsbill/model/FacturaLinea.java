package com.bitjawsbill.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "factura_lineas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaLinea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    @JsonBackReference
    private Factura factura;

    @OneToMany(mappedBy = "linea")
    @JsonManagedReference
    private List<FacturaImpuesto> impuestos = new ArrayList<>();

    private String descripcion;
	private String unidad;
	private String codigo;
	private String codigoProducto;
	private String descripcionProducto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal totalLinea;

    // Getters y Setters
}