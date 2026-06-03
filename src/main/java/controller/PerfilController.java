package controller;

import bll.CodigoPostalService;
import bll.UtilizadorService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import model.CodigoPostal;
import model.Utilizador;
import model.dto.PerfilForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PerfilController {

    private final UtilizadorService utilizadorService;
    private final CodigoPostalService codigoPostalService;

    public PerfilController(UtilizadorService utilizadorService,
                            CodigoPostalService codigoPostalService) {
        this.utilizadorService = utilizadorService;
        this.codigoPostalService = codigoPostalService;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) return "redirect:/login";

        Utilizador u = utilizadorService.buscarPorId(utilizadorId);
        model.addAttribute("perfilForm", toForm(u));
        model.addAttribute("nomeCompleto", formatarNomeCompleto(u));
        model.addAttribute("tipoUtilizador", formatarTipo(u.getTipoUtilizador()));
        return "perfil/index";
    }

    @PostMapping("/perfil")
    public String atualizarPerfil(
            @Valid @ModelAttribute("perfilForm") PerfilForm form,
            BindingResult result,
            HttpSession session,
            Model model
    ) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) return "redirect:/login";

        if (result.hasErrors()) {
            Utilizador u = utilizadorService.buscarPorId(utilizadorId);
            model.addAttribute("nomeCompleto",   formatarNomeCompleto(u));
            model.addAttribute("tipoUtilizador", formatarTipo(u.getTipoUtilizador()));
            return "perfil/index";
        }

        try {
            Utilizador u = utilizadorService.buscarPorId(utilizadorId);
            aplicarForm(u, form);
            Utilizador atualizado = utilizadorService.salvar(u);

            session.setAttribute("utilizadorNome", formatarNomeCompleto(atualizado));

            return "redirect:/perfil?atualizado=true";
        } catch (RuntimeException ex) {
            result.reject("erroPerfil", ex.getMessage());
            Utilizador u = utilizadorService.buscarPorId(utilizadorId);
            model.addAttribute("nomeCompleto",   formatarNomeCompleto(u));
            model.addAttribute("tipoUtilizador", formatarTipo(u.getTipoUtilizador()));
            return "perfil/index";
        }
    }

    private PerfilForm toForm(Utilizador u) {
        PerfilForm f = new PerfilForm();
        f.setPrimeiroNome(u.getPrimeiroNome());
        f.setUltimoNome(u.getUltimoNome());
        f.setDataNascimento(u.getDataNascimento());
        f.setNif(u.getNif());
        f.setEmail(u.getEmail());
        f.setTelemovel(u.getTelemovel());
        f.setTelefone(u.getTelefone());
        f.setRua(u.getRua());
        f.setNumeroPorta(u.getNumeroPorta());
        f.setCodigoPostal(
            u.getCodigoPostal() != null ? u.getCodigoPostal().getCodigoPostal() : null
        );
        f.setLocalidade(
            u.getCodigoPostal() != null ? u.getCodigoPostal().getLocalidade() : null
        );
        f.setTipoUtilizador(formatarTipo(u.getTipoUtilizador()));
        return f;
    }

    private void aplicarForm(Utilizador u, PerfilForm f) {
        u.setPrimeiroNome(f.getPrimeiroNome());
        u.setUltimoNome(f.getUltimoNome());
        u.setDataNascimento(f.getDataNascimento());
        u.setNif(emptyToNull(f.getNif()));
        u.setEmail(f.getEmail());
        u.setTelemovel(emptyToNull(f.getTelemovel()));
        u.setTelefone(emptyToNull(f.getTelefone()));
        u.setRua(emptyToNull(f.getRua()));
        u.setNumeroPorta(emptyToNull(f.getNumeroPorta()));

        String cp = emptyToNull(f.getCodigoPostal());
        if (cp != null) {
            try {
                CodigoPostal codigoPostal = codigoPostalService.buscarPorId(cp);
                u.setCodigoPostal(codigoPostal);
            } catch (RuntimeException ex) {
                u.setCodigoPostal(null);
            }
        } else {
            u.setCodigoPostal(null);
        }
    }

    private String formatarNomeCompleto(Utilizador u) {
        String p = u.getPrimeiroNome() != null ? u.getPrimeiroNome().trim() : "";
        String l = u.getUltimoNome()   != null ? u.getUltimoNome().trim()   : "";
        String nome = (p + " " + l).trim();
        return nome.isBlank() ? (u.getEmail() != null ? u.getEmail() : "—") : nome;
    }

    private String formatarTipo(String tipo) {
        if (tipo == null) return "Utilizador";
        return switch (tipo.toUpperCase()) {
            case "PACIENTE"      -> "Paciente";
            case "DENTISTA"      -> "Dentista";
            case "RECEPCIONISTA" -> "Recepcionista";
            case "ASSISTENTE"    -> "Assistente";
            case "ADMIN"         -> "Administrador";
            default              -> tipo;
        };
    }

    private String emptyToNull(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }
}
