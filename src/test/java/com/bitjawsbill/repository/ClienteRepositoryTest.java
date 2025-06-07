package com.bitjawsbill.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bitjawsbill.model.Cliente;
import com.bitjawsbill.model.Organizacion;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    public void whenFindByOrganizacion_thenReturnClienteList() {
        // given
        Organizacion organizacion = new Organizacion();
        entityManager.persist(organizacion);
        entityManager.flush();

        Cliente cliente = new Cliente();
        cliente.setOrganizacion(organizacion);
        entityManager.persist(cliente);
        entityManager.flush();

        // when
        var found = clienteRepository.findByOrganizacion(organizacion);

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getOrganizacion()).isEqualTo(organizacion);
    }
} 