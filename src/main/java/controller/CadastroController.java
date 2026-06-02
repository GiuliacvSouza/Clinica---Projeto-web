package controller;

import bll.PacienteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import model.Paciente;
import model.Utilizador;
import model.dto.CadastroForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Controller
public class CadastroController {

    private final PacienteService pacienteService;

    public CadastroController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /** Redireciona utilizador já autenticado; caso contrário apresenta o formulário vazio. */
    @GetMapping("/cadastro")
    public String mostrarFormulario(HttpSession session, Model model) {
        if (session.getAttribute("utilizadorId") != null) {
            return "redirect:/consultas";
        }
        model.addAttribute("cadastroForm", new CadastroForm());
        return "cadastro/index";
    }

    /**
     * Processa o registo.
     * Spring valida {@link CadastroForm} antes de entrar no método.
     * Se houver erros, o formulário é reapresentado — o Thymeleaf mostra os erros.
     */
    @PostMapping("/cadastro")
    public String processar(
            @Valid @ModelAttribute("cadastroForm") CadastroForm form,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            // Spring devolve o formulário com os erros já ligados ao model attribute
            return "cadastro/index";
        }

        try {
            Paciente paciente = new Paciente();
            paciente.setUtilizador(criarUtilizador(form));
            paciente.setStatus("ATIVO");
            paciente.setDataRegisto(LocalDate.now());

            pacienteService.salvar(paciente);
            return "redirect:/login?cadastro=sucesso";

        } catch (RuntimeException ex) {
            // Erro de negócio (ex: e-mail já registado) — apresentado como erro global
            result.reject("erroCadastro", ex.getMessage());
            return "cadastro/index";
        }
    }

    // ── Utilitário ────────────────────────────────────────────────────────────

    private Utilizador criarUtilizador(CadastroForm form) {
        String[] partes = form.getNome().split("[\\s\\-]+", 2);

        Utilizador u = new Utilizador();
        u.setPrimeiroNome(partes[0]);
        u.setUltimoNome(partes.length > 1 ? partes[1] : "");
        u.setEmail(form.getEmail());
        u.setTelemovel(form.getTelefone() == null || form.getTelefone().isBlank()
                ? null : form.getTelefone());
        u.setTipoUtilizador("PACIENTE");
        u.setSenha(form.getPassword());  // hash é feito no UtilizadorService
        u.setStatus("ATIVO");
        return u;
    }
}
