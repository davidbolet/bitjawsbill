package com.bitjawsbill.service;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bitjawsbill.model.Cliente;
import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.Organizacion;
import com.bitjawsbill.model.TipoFactura;
import com.bitjawsbill.model.TipoImpuesto;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.model.dto.FacturaDTO;
import com.bitjawsbill.repository.ClienteRepository;
import com.bitjawsbill.repository.FacturaImpuestoRepository;
import com.bitjawsbill.repository.FacturaLineaRepository;
import com.bitjawsbill.repository.FacturaRepository;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {

    @Mock private ClienteRepository clienteRepo;
    @Mock private FacturaRepository facturaRepo;
    @Mock private FacturaLineaRepository lineaRepo;
    @Mock private FacturaImpuestoRepository impuestoRepo;

    @InjectMocks
    private FacturaServiceImpl facturaService;

    private Organizacion org;
    private Usuario usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        org = new Organizacion();
        org.setId(1L);

        usuario = new Usuario();
        usuario.setOrganizacion(org);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setOrganizacion(org);
    }

    @Test
    void crearFactura_conDatosValidos_devuelveFacturaPersistida() {
        FacturaDTO dto = new FacturaDTO();
        dto.numeroFactura = "F0001";
        dto.serie = "A";
        dto.tipoFactura = TipoFactura.FACTURA;
        dto.moneda = "EUR";
        dto.descripcion = "Servicios prestados";
        dto.clienteId = 1L;

        FacturaDTO.LineaDTO linea = new FacturaDTO.LineaDTO();
        linea.descripcion = "Servicio mensual";
        linea.cantidad = new BigDecimal("1");
        linea.precioUnitario = new BigDecimal("100");
        linea.descuento = new BigDecimal("0");

        FacturaDTO.ImpuestoDTO impuesto = new FacturaDTO.ImpuestoDTO();
        impuesto.tipo = TipoImpuesto.IVA;
        impuesto.porcentaje = new BigDecimal("21");

        linea.impuestos = List.of(impuesto);
        dto.lineas = List.of(linea);

        when(clienteRepo.findById(1L)).thenReturn(Optional.of(cliente));
        when(facturaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Factura factura = facturaService.crearFactura(dto, usuario);

        assertNotNull(factura);
        assertEquals("F0001", factura.getNumeroFactura());
        assertEquals(new BigDecimal("100"), factura.getTotalBase());
        assertEquals(new BigDecimal("21"), factura.getTotalImpuestos());
        assertEquals(new BigDecimal("121"), factura.getTotalFactura());
    }
}