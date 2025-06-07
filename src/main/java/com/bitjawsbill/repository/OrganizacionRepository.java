package com.bitjawsbill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitjawsbill.model.Organizacion;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {

	
}
