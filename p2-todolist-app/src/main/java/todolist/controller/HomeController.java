package todolist.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import todolist.service.UsuarioService;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/registered")
    public String showRegisteredUsers(Model model) {
        model.addAttribute("users", usuarioService.findAll());
        return "registeredUsers";
    }
}