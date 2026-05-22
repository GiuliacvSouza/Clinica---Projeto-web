package controller;

import bll.PacienteService;
import jakarta.servlet.http.HttpSession;
import model.Paciente;
import model.Utilizador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class CadastroController {

    private final PacienteService pacienteService;

    public CadastroController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/cadastro")
    public String cadastro(HttpSession session) {
        if (session.getAttribute("utilizadorId") != null) {
            return "redirect:/consultas";
        }
        return "cadastro/index";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false) String telefone,
            @RequestParam String password,
            @RequestParam(required = false) String terms,
            Model model
    ) {
        if (terms == null) {
            return erroCadastro(model, nome, email, telefone, "Tem de aceitar os termos para criar a conta.");
        }

        try {
            Paciente paciente = new Paciente();
            paciente.setUtilizador(criarUtilizador(nome, email, telefone, password));
            paciente.setStatus("ATIVO");
            paciente.setDataRegisto(LocalDate.now());

            pacienteService.salvar(paciente);
            return "redirect:/login?cadastro=sucesso";
        } catch (RuntimeException ex) {
            return erroCadastro(model, nome, email, telefone, ex.getMessage());
        }
    }

    private Utilizador criarUtilizador(String nome, String email, String telefone, String password) {
        String nomeNormalizado = nome != null ? nome.trim() : "";
        if (nomeNormalizado.isBlank()) {
            throw new RuntimeException("Nome completo e obrigatorio.");
        }

        String[] partesNome = nomeNormalizado.split("\\s+", 2);

        Utilizador utilizador = new Utilizador();
        utilizador.setPrimeiroNome(partesNome[0]);
        utilizador.setUltimoNome(partesNome.length > 1 ? partesNome[1] : "");
        utilizador.setEmail(email);
        utilizador.setTelemovel(telefone);
        utilizador.setTipoUtilizador("PACIENTE");
        utilizador.setSenha(password);
        utilizador.setStatus("ATIVO");
        return utilizador;
    }

    private String erroCadastro(Model model, String nome, String email, String telefone, String mensagem) {
        model.addAttribute("erroCadastro", mensagem);
        model.addAttribute("nomeInformado", nome);
        model.addAttribute("emailInformado", email);
        model.addAttribute("telefoneInformado", telefone);
        return "cadastro/index";
    }
}
