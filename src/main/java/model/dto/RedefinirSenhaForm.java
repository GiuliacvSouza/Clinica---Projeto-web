package model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import validation.ValidPassword;

/**
 * DTO para o formulário de redefinição de palavra-passe.
 * O token vem como hidden input — não é validado aqui (é validado no controller).
 */
public class RedefinirSenhaForm {

    /** Enviado como hidden input — não exposto ao utilizador. */
    @NotBlank
    private String token;

    @NotBlank(message = "A palavra-passe é obrigatória.")
    @ValidPassword
    private String novaSenha;

    @NotBlank(message = "A confirmação da palavra-passe é obrigatória.")
    private String confirmarSenha;

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }

    public String getConfirmarSenha() { return confirmarSenha; }
    public void setConfirmarSenha(String confirmarSenha) { this.confirmarSenha = confirmarSenha; }
}
