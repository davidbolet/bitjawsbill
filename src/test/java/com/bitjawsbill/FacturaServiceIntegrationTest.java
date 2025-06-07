package com.bitjawsbill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.bitjawsbill.model.Cliente;
import com.bitjawsbill.model.Factura;
import com.bitjawsbill.model.Organizacion;
import com.bitjawsbill.model.TipoCliente;
import com.bitjawsbill.model.TipoFactura;
import com.bitjawsbill.model.TipoImpuesto;
import com.bitjawsbill.model.Usuario;
import com.bitjawsbill.repository.ClienteRepository;
import com.bitjawsbill.repository.FacturaRepository;
import com.bitjawsbill.repository.OrganizacionRepository;
import com.bitjawsbill.repository.UsuarioRepository;
import com.bitjawsbill.service.FacturaService;
import com.bitjawsbill.service.UsuarioService;
import com.bitjawsbill.model.dto.FacturaDTO;

@SpringBootTest(classes = BitjawsbillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class FacturaServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired private FacturaService facturaService;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private FacturaRepository facturaRepo;
    @Autowired private UsuarioService usuarioService;
    @Autowired private OrganizacionRepository organizacionRepo;
    @Autowired private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private String baseUrl;
    private HttpHeaders headers;
    private String password = "password";

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/facturas";
        
        // Limpiar la base de datos
        usuarioRepository.deleteAll();
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

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setTipoCliente(TipoCliente.FISICA);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setNif("12345678A");
        cliente.setEmail("cliente@test.com");
        cliente.setTelefono("123456789");
        cliente.setDireccion("Calle Test 123");
        cliente.setCiudad("Ciudad Test");
        cliente.setPais("País Test");
        cliente.setOrganizacion(org);
        clienteRepo.save(cliente);

        // Log para depurar: mostrar usuarios existentes y contraseña codificada
        System.out.println("Usuarios existentes antes del login:");
        usuarioRepository.findAll().forEach(u -> System.out.println("Usuario: " + u.getUsername() + ", Password: " + u.getPassword()));
        System.out.println("Password codificada esperada: " + password);

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

        System.out.println("Respuesta login: status=" + loginResponse.getStatusCode() + ", body=" + loginResponse.getBody());

        if (!loginResponse.getStatusCode().is2xxSuccessful() || loginResponse.getBody() == null || loginResponse.getBody().getToken() == null) {
            throw new RuntimeException("Error en login: status=" + loginResponse.getStatusCode() + ", body=" + loginResponse.getBody());
        }

        System.out.println("Token JWT recibido: " + loginResponse.getBody().getToken());
        return loginResponse.getBody().getToken();
    }

    @Test
    void crearFacturaYRecuperarPorREST() {
        // Verificar que el usuario existe antes de la prueba
        Usuario usuarioTest = usuarioService.getUsuarioAutenticado(() -> "admin");
        assertNotNull(usuarioTest, "El usuario de prueba debe existir");

        // Crear DTO de factura
        FacturaDTO dto = new FacturaDTO();
        dto.numeroFactura = "F001";
        dto.serie = "A";
        dto.tipoFactura = TipoFactura.FACTURA;
        dto.moneda = "EUR";
        dto.descripcion = "Factura de prueba";
        dto.clienteId = clienteRepo.findAll().get(0).getId();

        // Crear línea de factura
        FacturaDTO.LineaDTO linea = new FacturaDTO.LineaDTO();
        linea.descripcion = "Producto de prueba";
        linea.cantidad = new BigDecimal("1");
        linea.precioUnitario = new BigDecimal("100.00");
        linea.descuento = BigDecimal.ZERO;
        linea.unidad = "unidad";
        linea.codigo = "COD001";
        linea.codigoProducto = "PROD001";
        linea.descripcionProducto = "Descripción del producto de prueba";

        // Crear impuesto
        FacturaDTO.ImpuestoDTO impuesto = new FacturaDTO.ImpuestoDTO();
        impuesto.tipo = TipoImpuesto.IVA;
        impuesto.porcentaje = new BigDecimal("21.00");

        // Asociar impuesto a la línea
        linea.impuestos = new ArrayList<>();
        linea.impuestos.add(impuesto);

        // Asociar línea al DTO
        dto.lineas = new ArrayList<>();
        dto.lineas.add(linea);

        // Enviar petición POST
        HttpEntity<FacturaDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<Factura> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.POST,
            request,
            Factura.class
        );

        // Verificar respuesta
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        // Recuperar factura
        ResponseEntity<Factura> getResponse = restTemplate.exchange(
            baseUrl + "/" + response.getBody().getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            Factura.class
        );

        // Verificar factura recuperada
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(dto.numeroFactura, getResponse.getBody().getNumeroFactura());
        assertEquals(new BigDecimal("121.00"), getResponse.getBody().getTotalFactura());
        assertEquals(1, getResponse.getBody().getLineas().size());
        assertEquals(1, getResponse.getBody().getLineas().get(0).getImpuestos().size());
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