package com.bitjawsbill.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitjawsbill.model.Cliente;
import com.bitjawsbill.model.EstadoFactura;
import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.FacturaImpuesto;
import com.bitjawsbill.model.FacturaLinea;
import com.bitjawsbill.model.Organizacion;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.model.dto.FacturaDTO;
import com.bitjawsbill.repository.ClienteRepository;
import com.bitjawsbill.repository.FacturaImpuestoRepository;
import com.bitjawsbill.repository.FacturaLineaRepository;
import com.bitjawsbill.repository.FacturaRepository;

@Service
public class FacturaServiceImpl implements FacturaService {

    private static final Logger logger = LoggerFactory.getLogger(FacturaServiceImpl.class);

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private FacturaRepository facturaRepo;

    @Autowired
    private FacturaLineaRepository lineaRepo;

    @Autowired
    private FacturaImpuestoRepository impuestoRepo;

    @Override
    @Transactional
    public Factura crearFactura(FacturaDTO dto, Usuario usuarioActual) {
        logger.info("Iniciando creación de factura para usuario: {}", usuarioActual.getUsername());
        logger.debug("Datos de la factura: {}", dto);

        Organizacion organizacion = usuarioActual.getOrganizacion();
        logger.debug("Organización: {}", organizacion.getNombre());
		// Checquar si existe la misma factura en la organizacion
		Factura facturaExistente = facturaRepo.findByOrganizacionAndNumeroFacturaAndSerie(organizacion, dto.numeroFactura, dto.serie);
		if (facturaExistente != null) {
			logger.error("Factura ya existe. ID: {}, Organización: {}", facturaExistente.getId(), organizacion.getId());
			throw new IllegalArgumentException("Factura ya existe.");
		}

        // Validar cliente pertenece a la organización
        Cliente cliente = clienteRepo.findById(dto.clienteId)
            .filter(c -> c.getOrganizacion().getId().equals(organizacion.getId()))
            .orElseThrow(() -> {
                logger.error("Cliente no válido. ID: {}, Organización: {}", dto.clienteId, organizacion.getId());
                return new IllegalArgumentException("Cliente no válido o no pertenece a la organización.");
            });
        logger.debug("Cliente validado: {}", cliente.getNombre());

        // Crear factura
        Factura factura = new Factura();
        factura.setNumeroFactura(dto.numeroFactura);
        factura.setSerie(dto.serie);
        factura.setFechaEmision(LocalDate.now());
        factura.setDescripcion(dto.descripcion);
        factura.setTipoFactura(dto.tipoFactura);
        factura.setEstado(EstadoFactura.EMITIDA);
        factura.setEsRecibida(false);
        factura.setMoneda(dto.moneda);
        factura.setCliente(cliente);
        factura.setEmisor(organizacion.getCliente());
        factura.setOrganizacion(organizacion);

        logger.debug("Factura creada con número: {}", factura.getNumeroFactura());
        facturaRepo.save(factura);

        BigDecimal totalBase = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;

        // Procesar líneas de factura
        if (dto.lineas != null) {
            logger.debug("Procesando {} líneas de factura", dto.lineas.size());
            for (FacturaDTO.LineaDTO lineaDTO : dto.lineas) {
                BigDecimal bruto = lineaDTO.cantidad.multiply(lineaDTO.precioUnitario);
                BigDecimal neto = bruto.subtract(lineaDTO.descuento == null ? BigDecimal.ZERO : lineaDTO.descuento);

                FacturaLinea linea = new FacturaLinea();
                linea.setFactura(factura);
                linea.setDescripcion(lineaDTO.descripcion);
                linea.setCantidad(lineaDTO.cantidad);
                linea.setPrecioUnitario(lineaDTO.precioUnitario);
                linea.setDescuento(lineaDTO.descuento);
                linea.setTotalLinea(neto);
                linea.setUnidad(lineaDTO.unidad);
                linea.setCodigo(lineaDTO.codigo);
                linea.setCodigoProducto(lineaDTO.codigoProducto);
                linea.setDescripcionProducto(lineaDTO.descripcionProducto);

                lineaRepo.save(linea);
                factura.getLineas().add(linea);
                totalBase = totalBase.add(neto);

                logger.debug("Línea procesada: {} - Importe base: {}", linea.getDescripcion(), neto);

                // Procesar impuestos de la línea
                if (lineaDTO.impuestos != null) {
                    for (FacturaDTO.ImpuestoDTO impDTO : lineaDTO.impuestos) {
                        BigDecimal importe = neto.multiply(impDTO.porcentaje).divide(BigDecimal.valueOf(100));

                        FacturaImpuesto impuesto = new FacturaImpuesto();
                        impuesto.setFactura(factura);
                        impuesto.setLinea(linea);
                        impuesto.setTipoImpuesto(impDTO.tipo);
                        impuesto.setPorcentaje(impDTO.porcentaje);
                        impuesto.setImporte(importe);
                        impuestoRepo.save(impuesto);

                        totalImpuestos = totalImpuestos.add(importe);
                        logger.debug("Impuesto procesado: {} - Importe: {}", impuesto.getTipoImpuesto(), impuesto.getImporte());
                    }
                }
            }
        }

        // Actualizar totales
        factura.setTotalBase(totalBase);
        factura.setTotalImpuestos(totalImpuestos);
        factura.setTotalFactura(totalBase.add(totalImpuestos));
        
        logger.info("Factura creada exitosamente. ID: {}, Total: {}", factura.getId(), factura.getTotalFactura());
        return facturaRepo.save(factura);
    }

    @Override
    public List<Factura> listarFacturasPorOrganizacion(Organizacion organizacion) {
        logger.info("Listando facturas para organización: {}", organizacion.getNombre());
        List<Factura> facturas = facturaRepo.findByOrganizacion(organizacion);
        logger.debug("Se encontraron {} facturas", facturas.size());
        return facturas;
    }

    @Override
    public Factura obtenerFacturaPorIdYOrganizacion(Long id, Organizacion organizacion) {
        logger.info("Buscando factura ID: {} para organización: {}", id, organizacion.getNombre());
        return facturaRepo.findById(id)
            .filter(f -> f.getOrganizacion().getId().equals(organizacion.getId()))
            .orElseThrow(() -> {
                logger.error("Factura no encontrada o no pertenece a la organización. ID: {}, Organización: {}", 
                    id, organizacion.getNombre());
                return new RuntimeException("Factura no encontrada");
            });
    }
}