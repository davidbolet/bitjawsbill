package com.bitjawsbill.service;

import com.bitjawsbill.model.dto.ClienteDTO;

import java.security.Principal;
import java.util.List;

public interface ClienteService {
    List<ClienteDTO> findAll(Principal principal);
    ClienteDTO findById(Long id, Principal principal);
    ClienteDTO save(ClienteDTO clienteDTO, Principal principal);
    ClienteDTO update(ClienteDTO clienteDTO, Principal principal);
    void delete(Long id, Principal principal);
	Object findByIdAndOrganizacion(Long id, Principal principal);
} 