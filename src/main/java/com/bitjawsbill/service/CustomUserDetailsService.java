package com.bitjawsbill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitjawsbill.repository.UsuarioRepository;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
	@Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
            .map(usuario -> new User(
                usuario.getUsername(),
                usuario.getPasswordHash(),
                usuario.getRoles().stream()
                    .map(rol -> new SimpleGrantedAuthority(rol.getNombre().name()))
                    .collect(Collectors.toList())
            ))
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
} 