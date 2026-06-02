package model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import validation.PasswordMatches;
import validation.ValidPassword;
import validation.ValidTelefonePortugues;

/**
 * DTO de registo de novo utilizador.
 *
 * Anotações de campo tratam regras simples.
 * {@link PasswordMatches} é uma anotação de classe para validação cruzada.
 */
@PasswordMatches
public class CadastroForm {

    /**
     * Nome completo — obrigatório, mínimo 2 palavras, apenas letras (incl. acentos) e espaços/hífens.
     * (?U) ativa UNICODE_CHARACTER_CLASS para que \p{L} cubra letras acentuadas.
     */
    @NotBlank(message = "Introduza o seu nome completo.")
    @Pattern(
        regexp  = "(?U)^[\\p{L}]+([ \\-][\\p{L}]+)+$",
        message = "Introduza o seu nome completo (pelo menos dois nomes, sem números ou símbolos)."
    )
    private String nome;

    @NotBlank(message = "Introduza um e-mail válido.")
    @Email(message = "Introduza um e-mail válido.")
    private String email;

    /** Campo opcional — validador trata null/blank como válido. */
    @ValidTelefonePortugues
    private String telefone;

    @NotBlank(message = "A palavra-passe é obrigatória.")
    @ValidPassword
    private String password;

    @NotBlank(message = "A confirmação da palavra-passe é obrigatória.")
    private String confirmPassword;

    /**
     * Termos e condições.
     * {@code @AssertTrue} exige que o valor seja {@code true}.
     * O campo é um boolean porque o Thymeleaf com th:field em checkbox
     * envia "true"/"false" e o Spring converte automaticamente.
     */
    @AssertTrue(message = "Tem de aceitar os Termos de Serviço e a Política de Privacidade.")
    private boolean termos;

    // ── Getters e Setters ──────────────────────────────────────────────────────

    public String getNome() { return nome; }
    public void setNome(String nome) {
        this.nome = nome != null ? nome.trim() : null;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) {
        this.telefone = telefone != null ? telefone.trim() : null;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public boolean isTermos() { return termos; }
    public void setTermos(boolean termos) { this.termos = termos; }
}
