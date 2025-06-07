package com.bitjawsbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitjawsbill.model.FacturaImpuesto;

@Repository
public interface FacturaImpuestoRepository extends JpaRepository<FacturaImpuesto, Long> {
} 