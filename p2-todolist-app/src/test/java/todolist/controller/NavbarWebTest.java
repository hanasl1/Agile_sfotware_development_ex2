package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class NavbarWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void aboutPageShowsLoginAndRegisterWhenUserIsNotLoggedIn() throws Exception {
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Login"),
                        containsString("Register"),
                        containsString("About"),
                        not(containsString("Log out"))
                )));
    }

    @Test
    public void tasksPageShowsNavbarDropdownWhenUserIsLoggedIn() throws Exception {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("john.doe@example.com");
        usuario.setPassword("1234");
        usuario.setNombre("John Doe");
        usuario = usuarioService.registrar(usuario);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        this.mockMvc.perform(get("/usuarios/" + usuario.getId() + "/tareas"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Tasks"),
                        containsString("Log out John Doe"),
                        containsString("ToDoList")
                )));
    }
}
