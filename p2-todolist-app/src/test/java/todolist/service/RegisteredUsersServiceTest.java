package todolist.service;

import todolist.dto.UsuarioData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class RegisteredUsersServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void findAllReturnsAllRegisteredUsers() {
        UsuarioData user1 = new UsuarioData();
        user1.setEmail("service1@example.com");
        user1.setPassword("pass1");
        usuarioService.registrar(user1);

        UsuarioData user2 = new UsuarioData();
        user2.setEmail("service2@example.com");
        user2.setPassword("pass2");
        usuarioService.registrar(user2);

        List<UsuarioData> usuarios = usuarioService.findAll();

        assertThat(usuarios).hasSize(2);
        assertThat(usuarios).extracting(UsuarioData::getEmail)
                .containsExactlyInAnyOrder("service1@example.com", "service2@example.com");
    }

    @Test
    public void findByIdReturnsRegisteredUserDetails() {
        UsuarioData user = new UsuarioData();
        user.setEmail("service3@example.com");
        user.setPassword("pass3");
        user.setNombre("Service User");
        user.setFechaNacimiento(new java.util.Date(0));

        UsuarioData registered = usuarioService.registrar(user);

        UsuarioData found = usuarioService.findById(registered.getId());

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("service3@example.com");
        assertThat(found.getNombre()).isEqualTo("Service User");
        assertThat(found.getFechaNacimiento()).isNotNull();
    }
}
