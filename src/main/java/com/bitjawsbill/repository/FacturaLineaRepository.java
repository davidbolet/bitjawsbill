package com.bitjawsbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitjawsbill.model.FacturaLinea;

@Repository
public interface FacturaLineaRepository extends JpaRepository<FacturaLinea, Long> {
} 