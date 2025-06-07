package com.bitjawsbill.model.dto;

import java.math.BigDecimal;
import java.util.List;

import com.bitjawsbill.model.TipoFactura;
import com.bitjawsbill.model.TipoImpuesto;

import lombok.Data;

@Data
public class FacturaDTO {
    public Long clienteId;
    public List<LineaDTO> lineas;
    public String numeroFactura;
    public String serie;
    public TipoFactura tipoFactura;
    public String descripcion;
    public String moneda;

    public static class LineaDTO {
        public String descripcion;
        public BigDecimal cantidad;
        public BigDecimal precioUnitario;
        public BigDecimal descuento;
        public List<ImpuestoDTO> impuestos;
		public String unidad;
		public String codigo;
		public String codigoProducto;
		public String descripcionProducto;
    }

    public static class ImpuestoDTO {
        public TipoImpuesto tipo;
        public BigDecimal porcentaje;
    }
}