package com.bitjawsbill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.Organizacion;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
	List<Factura> findByOrganizacion(Organizacion organizacion);
	Factura findByIdAndOrganizacion(Long id, Organizacion organizacion);
	Factura findByOrganizacionAndNumeroFacturaAndSerie(Organizacion organizacion, String numeroFactura, String serie);
} 