package todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;

    @ModelAttribute
    public void addUsuarioModelAttributes(Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        if (idUsuario != null) {
            UsuarioData usuario = usuarioService.findById(idUsuario);
            if (usuario != null) {
                String usuarioNombre = usuario.getNombre();
                if (usuarioNombre == null || usuarioNombre.isBlank()) {
                    usuarioNombre = usuario.getEmail();
                }
                model.addAttribute("usuarioLogeado", true);
                model.addAttribute("usuarioName", usuarioNombre);
                model.addAttribute("usuarioId", idUsuario);
                return;
            }
        }
        model.addAttribute("usuarioLogeado", false);
    }
}
