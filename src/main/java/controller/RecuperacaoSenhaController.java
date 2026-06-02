package controller;

import bll.RecuperacaoSenhaService;
import jakarta.validation.Valid;
import model.PasswordResetToken;
import model.dto.RedefinirSenhaForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    public RecuperacaoSenhaController(RecuperacaoSenhaService recuperacaoSenhaService) {
        this.recuperacaoSenhaService = recuperacaoSenhaService;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PASSO 1 — Pedir recuperação (introduzir e-mail)
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/recuperar-senha")
    public String mostrarFormularioRecuperacao() {
        return "recuperar-senha/index";
    }

    @PostMapping("/recuperar-senha")
    public String processarPedidoRecuperacao(
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            recuperacaoSenhaService.iniciarRecuperacao(email);
        } catch (RuntimeException ex) {
            // Erro de infra (ex: SMTP indisponível) — mostrar mensagem genérica
            // sem revelar a causa real
            System.err.println("[RecuperacaoSenhaController] " + ex.getMessage());
        }

        // Mensagem sempre genérica — nunca revelar se o e-mail existe
        redirectAttributes.addFlashAttribute("mensagemEnviada",
                "Se o e-mail estiver registado, receberá instruções para redefinir a palavra-passe.");
        return "redirect:/recuperar-senha?enviado=true";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PASSO 2 — Redefinir a senha com o token recebido por e-mail
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/redefinir-senha")
    public String mostrarFormularioRedefinicao(
            @RequestParam(required = false) String token,
            Model model
    ) {
        if (token == null || token.isBlank()) {
            model.addAttribute("erroToken", "O link de recuperação é inválido ou expirou.");
            return "redefinir-senha/index";
        }

        Optional<PasswordResetToken> prt = recuperacaoSenhaService.validarToken(token);
        if (prt.isEmpty()) {
            model.addAttribute("erroToken", "O link de recuperação é inválido ou expirou.");
            return "redefinir-senha/index";
        }

        // Token válido — mostrar formulário de nova senha
        RedefinirSenhaForm form = new RedefinirSenhaForm();
        form.setToken(token);
        model.addAttribute("redefinirSenhaForm", form);
        return "redefinir-senha/index";
    }

    @PostMapping("/redefinir-senha")
    public String processarRedefinicao(
            @Valid @ModelAttribute("redefinirSenhaForm") RedefinirSenhaForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // Validar confirmação de senha (cross-field — não coberto por @Valid sozinho)
        if (!result.hasFieldErrors("novaSenha")
                && form.getNovaSenha() != null
                && !form.getNovaSenha().equals(form.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "senhas.nao.coincidem",
                    "As palavras-passe não coincidem.");
        }

        if (result.hasErrors()) {
            // Revalidar token antes de reapresentar formulário
            if (form.getToken() != null && recuperacaoSenhaService.validarToken(form.getToken()).isEmpty()) {
                model.addAttribute("erroToken", "O link de recuperação é inválido ou expirou.");
                return "redefinir-senha/index";
            }
            return "redefinir-senha/index";
        }

        try {
            recuperacaoSenhaService.redefinirSenha(
                    form.getToken(),
                    form.getNovaSenha(),
                    form.getConfirmarSenha()
            );
        } catch (IllegalStateException ex) {
            // Token inválido ou expirado
            model.addAttribute("erroToken", ex.getMessage());
            return "redefinir-senha/index";
        } catch (IllegalArgumentException ex) {
            // Senhas não coincidem (segunda linha de defesa)
            result.rejectValue("confirmarSenha", "senhas.nao.coincidem", ex.getMessage());
            return "redefinir-senha/index";
        }

        redirectAttributes.addFlashAttribute("senhaRedefinida",
                "A sua palavra-passe foi alterada com sucesso. Já pode iniciar sessão.");
        return "redirect:/login?redefinida=true";
    }
}
