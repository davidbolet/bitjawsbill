package com.bitjawsbill.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.bitjawsbill.model.EstadoFactura;
import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.TipoFactura;

import lombok.Data;

@Data
public class FacturaResponseDTO {
    private Long id;
    private String numeroFactura;
    private String serie;
    private LocalDate fechaEmision;
    private String descripcion;
    private TipoFactura tipoFactura;
    private EstadoFactura estado;
    private Boolean esRecibida;
    private String moneda;
    private BigDecimal totalBase;
    private BigDecimal totalImpuestos;
    private BigDecimal totalFactura;
    private LocalDateTime fechaCreacion;
    private List<LineaResponseDTO> lineas;

    @Data
    public static class LineaResponseDTO {
        private Long id;
        private String descripcion;
        private String unidad;
        private String codigo;
        private String codigoProducto;
        private String descripcionProducto;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal totalLinea;
        private List<ImpuestoResponseDTO> impuestos;
    }

    @Data
    public static class ImpuestoResponseDTO {
        private Long id;
        private String tipo;
        private BigDecimal porcentaje;
        private BigDecimal importe;
    }

    public static FacturaResponseDTO fromEntity(Factura factura) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(factura.getId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setSerie(factura.getSerie());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setDescripcion(factura.getDescripcion());
        dto.setTipoFactura(factura.getTipoFactura());
        dto.setEstado(factura.getEstado());
        dto.setEsRecibida(factura.getEsRecibida());
        dto.setMoneda(factura.getMoneda());
        dto.setTotalBase(factura.getTotalBase());
        dto.setTotalImpuestos(factura.getTotalImpuestos());
        dto.setTotalFactura(factura.getTotalFactura());
        dto.setFechaCreacion(factura.getFechaCreacion());
        
        if (factura.getLineas() != null) {
            dto.setLineas(factura.getLineas().stream()
                .map(linea -> {
                    LineaResponseDTO lineaDTO = new LineaResponseDTO();
                    lineaDTO.setId(linea.getId());
                    lineaDTO.setDescripcion(linea.getDescripcion());
                    lineaDTO.setUnidad(linea.getUnidad());
                    lineaDTO.setCodigo(linea.getCodigo());
                    lineaDTO.setCodigoProducto(linea.getCodigoProducto());
                    lineaDTO.setDescripcionProducto(linea.getDescripcionProducto());
                    lineaDTO.setCantidad(linea.getCantidad());
                    lineaDTO.setPrecioUnitario(linea.getPrecioUnitario());
                    lineaDTO.setDescuento(linea.getDescuento());
                    lineaDTO.setTotalLinea(linea.getTotalLinea());
                    
                    if (linea.getImpuestos() != null) {
                        lineaDTO.setImpuestos(linea.getImpuestos().stream()
                            .map(impuesto -> {
                                ImpuestoResponseDTO impuestoDTO = new ImpuestoResponseDTO();
                                impuestoDTO.setId(impuesto.getId());
                                impuestoDTO.setTipo(impuesto.getTipoImpuesto().name());
                                impuestoDTO.setPorcentaje(impuesto.getPorcentaje());
                                impuestoDTO.setImporte(impuesto.getImporte());
                                return impuestoDTO;
                            })
                            .collect(Collectors.toList()));
                    }
                    return lineaDTO;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 