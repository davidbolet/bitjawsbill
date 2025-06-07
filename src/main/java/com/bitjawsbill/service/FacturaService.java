package com.bitjawsbill.service;

import java.util.List;

import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.Organizacion;
import com.bitjawsbill.model.dto.FacturaDTO;
import com.bitjawsbill.model.Usuario;

public interface FacturaService {
	Factura crearFactura(FacturaDTO facturaDTO, Usuario usuario);

	List<Factura> listarFacturasPorOrganizacion(Organizacion organizacion);

	Factura obtenerFacturaPorIdYOrganizacion(Long id, Organizacion organizacion);
}
