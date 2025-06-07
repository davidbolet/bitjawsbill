package com.bitjawsbill.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitjawsbill.model.Cliente;
import com.bitjawsbill.model.Organizacion;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByOrganizacion(Organizacion organizacion);

	Optional<Cliente> findByIdAndOrganizacion(Long id, Organizacion organizacion);
} 