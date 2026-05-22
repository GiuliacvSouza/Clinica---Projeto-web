package controller;

import bll.UtilizadorService;
import jakarta.servlet.http.HttpSession;
import model.Utilizador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UtilizadorService utilizadorService;

    public LoginController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @GetMapping({"/", "/login"})
    public String login(HttpSession session) {
        if (session.getAttribute("utilizadorId") != null) {
            return "redirect:/consultas";
        }
        return "login/index";
    }

    @PostMapping("/login")
    public String autenticar(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Utilizador utilizador = utilizadorService.autenticar(email, password);

        if (utilizador == null) {
            model.addAttribute("erroLogin", "E-mail ou senha invalidos.");
            model.addAttribute("emailInformado", email);
            return "login/index";
        }

        session.setAttribute("utilizadorId", utilizador.getId());
        session.setAttribute("utilizadorNome", formatarNome(utilizador));
        session.setAttribute("utilizadorTipo", utilizador.getTipoUtilizador());
        session.setAttribute("utilizadorNif", utilizador.getNif());

        return "redirect:/consultas";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/consultas";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String formatarNome(Utilizador utilizador) {
        String primeiroNome = utilizador.getPrimeiroNome() != null ? utilizador.getPrimeiroNome().trim() : "";
        String ultimoNome = utilizador.getUltimoNome() != null ? utilizador.getUltimoNome().trim() : "";
        String nomeCompleto = (primeiroNome + " " + ultimoNome).trim();
        return nomeCompleto.isBlank() ? utilizador.getEmail() : nomeCompleto;
    }
}
