package todolist.controller;

import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class RegisteredUsersWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void registeredPageShowsAllRegisteredUsersAndLinksToDescription() throws Exception {
        UsuarioData user1 = new UsuarioData();
        user1.setEmail("alice@example.com");
        user1.setPassword("pass1");
        UsuarioData registered1 = usuarioService.registrar(user1);

        UsuarioData user2 = new UsuarioData();
        user2.setEmail("bob@example.com");
        user2.setPassword("pass2");
        usuarioService.registrar(user2);

        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Registered users"),
                        containsString("alice@example.com"),
                        containsString("bob@example.com"),
                        containsString("Id"),
                        containsString("Email"),
                        containsString("/registered/" + registered1.getId())
                )));
    }

    @Test
    public void registeredUserDescriptionShowsDataWithoutPassword() throws Exception {
        UsuarioData user = new UsuarioData();
        user.setEmail("charlie@example.com");
        user.setPassword("secret");
        user.setNombre("Charlie Brown");
        user.setFechaNacimiento(new Date(0));
        UsuarioData registered = usuarioService.registrar(user);

        this.mockMvc.perform(get("/registered/" + registered.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("User description"),
                        containsString("charlie@example.com"),
                        containsString("Charlie Brown"),
                        containsString("Birth date"),
                        not(containsString("secret"))
                )));
    }
}
