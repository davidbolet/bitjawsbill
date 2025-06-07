package com.bitjawsbill.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitjawsbill.model.Rol;
import com.bitjawsbill.model.Rol.TipoRol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(TipoRol nombre);
} 