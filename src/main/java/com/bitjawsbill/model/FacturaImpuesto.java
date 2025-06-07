package com.bitjawsbill.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "factura_impuestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaImpuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
	@JsonIgnore
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "linea_id")
    @JsonBackReference
    private FacturaLinea linea;

    @Enumerated(EnumType.STRING)
    private TipoImpuesto tipoImpuesto;

    private BigDecimal porcentaje;
    private BigDecimal importe;

    // Getters y Setters
}