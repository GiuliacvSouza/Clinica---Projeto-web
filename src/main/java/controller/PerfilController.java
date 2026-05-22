package controller;

import bll.UtilizadorService;
import jakarta.servlet.http.HttpSession;
import model.Utilizador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class PerfilController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UtilizadorService utilizadorService;

    public PerfilController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) {
            return "redirect:/login";
        }

        carregarModelo(model, utilizadorService.buscarPorId(utilizadorId));
        return "perfil/index";
    }

    @PostMapping("/perfil")
    public String atualizarPerfil(
            @RequestParam String email,
            @RequestParam(required = false) String telemovel,
            @RequestParam(required = false) String morada,
            HttpSession session,
            Model model
    ) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) {
            return "redirect:/login";
        }

        Utilizador utilizador = utilizadorService.buscarPorId(utilizadorId);
        utilizador.setEmail(email);
        utilizador.setTelemovel(telemovel);
        utilizador.setRua(morada);

        try {
            Utilizador atualizado = utilizadorService.salvar(utilizador);
            session.setAttribute("utilizadorNome", formatarNome(atualizado));
            session.setAttribute("utilizadorNif", atualizado.getNif());
            carregarModelo(model, atualizado);
            model.addAttribute("perfilAtualizado", true);
            return "perfil/index";
        } catch (RuntimeException ex) {
            carregarModelo(model, utilizador);
            model.addAttribute("erroPerfil", ex.getMessage());
            return "perfil/index";
        }
    }

    private void carregarModelo(Model model, Utilizador utilizador) {
        model.addAttribute("nomeCompleto", formatarNome(utilizador));
        model.addAttribute("dataNascimento", formatarData(utilizador.getDataNascimento()));
        model.addAttribute("telemovel", valorOuVazio(utilizador.getTelemovel()));
        model.addAttribute("email", valorOuVazio(utilizador.getEmail()));
        model.addAttribute("morada", formatarMorada(utilizador));
    }

    private String formatarNome(Utilizador utilizador) {
        String primeiroNome = utilizador.getPrimeiroNome() != null ? utilizador.getPrimeiroNome().trim() : "";
        String ultimoNome = utilizador.getUltimoNome() != null ? utilizador.getUltimoNome().trim() : "";
        String nomeCompleto = (primeiroNome + " " + ultimoNome).trim();
        return nomeCompleto.isBlank() ? utilizador.getEmail() : nomeCompleto;
    }

    private String formatarData(LocalDate data) {
        return data == null ? "Nao informada" : DATE_FORMATTER.format(data);
    }

    private String formatarMorada(Utilizador utilizador) {
        String rua = valorOuVazio(utilizador.getRua());
        String porta = valorOuVazio(utilizador.getNumeroPorta());
        return (rua + (porta.isBlank() ? "" : ", " + porta)).trim();
    }

    private String valorOuVazio(String valor) {
        return valor == null ? "" : valor;
    }
}
