package com.bitjawsbill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.bitjawsbill.model.Organizacion;
import com.bitjawsbill.model.TipoCliente;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.model.dto.ClienteDTO;
import com.bitjawsbill.repository.ClienteRepository;
import com.bitjawsbill.repository.OrganizacionRepository;
import com.bitjawsbill.repository.UsuarioRepository;
import com.bitjawsbill.repository.FacturaRepository;
import com.bitjawsbill.repository.FacturaImpuestoRepository;
import com.bitjawsbill.repository.FacturaLineaRepository;
import com.bitjawsbill.service.UsuarioService;
@SpringBootTest(classes = BitjawsbillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ClienteControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepo;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private OrganizacionRepository organizacionRepo;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private FacturaRepository facturaRepo;
    @Autowired
    private FacturaImpuestoRepository facturaImpuestoRepo;
    @Autowired
    private FacturaLineaRepository facturaLineaRepo;

    private Usuario usuario;
    private String baseUrl;
    private HttpHeaders headers;
    private String password = "password";

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/clientes";
        
        usuarioRepository.deleteAll();
        // Eliminar en orden correcto para evitar errores de integridad referencial
        facturaImpuestoRepo.deleteAll();
        facturaLineaRepo.deleteAll();
        facturaRepo.deleteAll();
        clienteRepo.deleteAll();
        organizacionRepo.deleteAll();

        // Crear organización
        Organizacion org = new Organizacion();
        org.setNombre("Mi Empresa Test");
        org.setNif("B12345678");
        org.setEmailContacto("contacto@test.com");
        org.setTipoCliente(TipoCliente.JURIDICA);
        org = organizacionRepo.save(org);

        // Crear usuario
        usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setEmail("admin@empresa.com");
        usuario.setPassword(password);
        usuario.setOrganizacion(org);
        usuario = usuarioService.save(usuario);

        org.getUsuarios().add(usuario);
        
        // Forzar el flush de la transacción
        usuarioRepository.flush();

        // Obtener token JWT
        String token = obtenerTokenJWT();
        
        // Configurar headers con token JWT
        headers = new HttpHeaders();
        headers.setBearerAuth(token);
    }

    private String obtenerTokenJWT() {
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String loginBody = "{\"username\":\"admin\",\"password\":\"" + password + "\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginBody, loginHeaders);

        ResponseEntity<AuthResponse> loginResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/auth/login",
            HttpMethod.POST,
            loginRequest,
            AuthResponse.class
        );

        if (!loginResponse.getStatusCode().is2xxSuccessful() || loginResponse.getBody() == null || loginResponse.getBody().getToken() == null) {
            throw new RuntimeException("Error en login: status=" + loginResponse.getStatusCode() + ", body=" + loginResponse.getBody());
        }

        return loginResponse.getBody().getToken();
    }

    @Test
    void testCRUDCliente() {
        // Crear cliente
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setTipoCliente(TipoCliente.FISICA);
        clienteDTO.setNombre("Juan");
        clienteDTO.setApellido("Pérez");
        clienteDTO.setNif("12345678A");
        clienteDTO.setEmail("cliente@test.com");
        clienteDTO.setTelefono("123456789");
        clienteDTO.setDireccion("Calle Test 123");
        clienteDTO.setCiudad("Ciudad Test");
        clienteDTO.setPais("País Test");
        clienteDTO.setOrganizacionId(organizacionRepo.findAll().get(0).getId());

        HttpEntity<ClienteDTO> request = new HttpEntity<>(clienteDTO, headers);
        ResponseEntity<ClienteDTO> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.POST,
            request,
            ClienteDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(clienteDTO.getNombre(), response.getBody().getNombre());

        Long clienteId = response.getBody().getId();

        // Obtener cliente
        ResponseEntity<ClienteDTO> getResponse = restTemplate.exchange(
            baseUrl + "/" + clienteId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ClienteDTO.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(clienteId, getResponse.getBody().getId());

        // Actualizar cliente
        clienteDTO.setId(clienteId);
        clienteDTO.setNombre("Juan Actualizado");
        request = new HttpEntity<>(clienteDTO, headers);
        ResponseEntity<ClienteDTO> updateResponse = restTemplate.exchange(
            baseUrl + "/" + clienteId,
            HttpMethod.PUT,
            request,
            ClienteDTO.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("Juan Actualizado", updateResponse.getBody().getNombre());

        // Eliminar cliente
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/" + clienteId,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verificar que el cliente fue eliminado
        ResponseEntity<ClienteDTO> getDeletedResponse = restTemplate.exchange(
            baseUrl + "/" + clienteId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ClienteDTO.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getDeletedResponse.getStatusCode());
    }

    private static class AuthResponse {
        private String token;
        private String username;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
} 