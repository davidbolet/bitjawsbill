package com.bitjawsbill.service;

import java.security.Principal;
import java.util.List;

import com.bitjawsbill.model.Rol.TipoRol;
import com.bitjawsbill.model.Usuario;

public interface UsuarioService {
	Usuario getUsuarioAutenticado(Principal principal);
	Usuario save(Usuario usuario);
	Usuario addRoleToUser(Long userId, TipoRol roleName);
	Usuario removeRoleFromUser(Long userId, TipoRol roleName);
	List<Usuario> getUsersByRole(TipoRol roleName);
	boolean hasRole(Usuario usuario, TipoRol roleName);
}
