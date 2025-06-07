package com.bitjawsbill.service;

import com.bitjawsbill.model.Cliente;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.model.dto.ClienteDTO;
import com.bitjawsbill.repository.ClienteRepository;
import com.bitjawsbill.repository.OrganizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

	@Autowired
	private UsuarioService usuarioService;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Override
    public List<ClienteDTO> findAll(Principal principal) {
		Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        return clienteRepository.findByOrganizacion(usuario.getOrganizacion()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteDTO findById(Long id, Principal principal) {
        Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        if (!cliente.getOrganizacion().getId().equals(usuario.getOrganizacion().getId())) {
            throw new RuntimeException("No tienes permiso para acceder a este cliente");
        }
        
        return convertToDTO(cliente);
    }

    @Override
    public ClienteDTO save(ClienteDTO clienteDTO, Principal principal) {
        Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        Cliente cliente = convertToEntity(clienteDTO);
        cliente.setOrganizacion(usuario.getOrganizacion());
        return convertToDTO(clienteRepository.save(cliente));
    }

    @Override
    public ClienteDTO update(ClienteDTO clienteDTO, Principal principal) {
        Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        Cliente clienteExistente = clienteRepository.findById(clienteDTO.getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        if (!clienteExistente.getOrganizacion().getId().equals(usuario.getOrganizacion().getId())) {
            throw new RuntimeException("No tienes permiso para modificar este cliente");
        }
        
        Cliente cliente = convertToEntity(clienteDTO);
        cliente.setOrganizacion(usuario.getOrganizacion());
        return convertToDTO(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void delete(Long id, Principal principal) {
        Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        if (!cliente.getOrganizacion().getId().equals(usuario.getOrganizacion().getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este cliente");
        }
        
        // Comprobar si el cliente tiene facturas asociadas
        if (!cliente.getFacturas().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el cliente porque tiene facturas asociadas");
        }
        
        // Limpiar las cuentas bancarias
        cliente.getCuentasBancarias().clear();
        clienteRepository.save(cliente);
        
        // Ahora podemos eliminar el cliente
        clienteRepository.deleteById(id);
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setOrganizacionId(cliente.getOrganizacion().getId());
        dto.setTipoCliente(cliente.getTipoCliente());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        dto.setPais(cliente.getPais());
        dto.setNif(cliente.getNif());
        dto.setCif(cliente.getCif());
        return dto;
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setId(dto.getId());
        cliente.setOrganizacion(organizacionRepository.findById(dto.getOrganizacionId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada")));
        cliente.setTipoCliente(dto.getTipoCliente());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente.setPais(dto.getPais());
        cliente.setNif(dto.getNif());
        cliente.setCif(dto.getCif());
        return cliente;
    }

	@Override
	public Optional<Cliente> findByIdAndOrganizacion(Long id, Principal principal) {
		Usuario usuario = usuarioService.getUsuarioAutenticado(principal);
		return clienteRepository.findByIdAndOrganizacion(id, usuario.getOrganizacion());
	}
} 