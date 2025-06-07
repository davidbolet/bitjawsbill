package com.bitjawsbill.service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitjawsbill.model.Rol;
import com.bitjawsbill.model.Rol.TipoRol;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.repository.RolRepository;
import com.bitjawsbill.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario getUsuarioAutenticado(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return usuarioRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }

    @Override
    public Usuario save(Usuario usuario) {
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPassword()));
        usuario.setPassword(null); // Limpiar el password en texto plano
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario addRoleToUser(Long userId, TipoRol roleName) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Rol rol = rolRepository.findByNombre(roleName)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        usuario.getRoles().add(rol);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario removeRoleFromUser(Long userId, TipoRol roleName) {
        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Rol rol = rolRepository.findByNombre(roleName)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        usuario.getRoles().remove(rol);
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> getUsersByRole(TipoRol roleName) {
        Rol rol = rolRepository.findByNombre(roleName)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return usuarioRepository.findByRolesContaining(rol);
    }

    @Override
    public boolean hasRole(Usuario usuario, TipoRol roleName) {
        return usuario.getRoles().stream()
            .anyMatch(rol -> rol.getNombre() == roleName);
    }
} 