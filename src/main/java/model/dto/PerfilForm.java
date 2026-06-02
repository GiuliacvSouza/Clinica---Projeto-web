package model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import validation.ValidNif;
import validation.ValidTelefonePortugues;

import java.time.LocalDate;

/**
 * DTO utilizado no formulário de edição de perfil do paciente.
 *
 * Campos editáveis: primeiroNome, ultimoNome, dataNascimento, nif,
 *                   email, telemovel, telefone, rua, numeroPorta.
 *
 * Campos apenas leitura (não submetidos): tipoUtilizador, codigoPostal, localidade.
 */
public class PerfilForm {

    // ── Informações Pessoais ──────────────────────────────────────────────────

    @NotBlank(message = "Introduza um primeiro nome válido.")
    @Size(min = 2, message = "Introduza um primeiro nome válido.")
    @Pattern(
        regexp  = "(?U)^[\\p{L}]+([ \\-][\\p{L}]+)*$",
        message = "Introduza um primeiro nome válido."
    )
    private String primeiroNome;

    @NotBlank(message = "Introduza um último nome válido.")
    @Size(min = 2, message = "Introduza um último nome válido.")
    @Pattern(
        regexp  = "(?U)^[\\p{L}]+([ \\-][\\p{L}]+)*$",
        message = "Introduza um último nome válido."
    )
    private String ultimoNome;

    /** Tipo date — o Thymeleaf usa th:field e o browser abre o picker nativo. */
    @Past(message = "Introduza uma data de nascimento válida.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    @ValidNif
    private String nif;

    // ── Contacto ─────────────────────────────────────────────────────────────

    @NotBlank(message = "Introduza um e-mail válido.")
    @Email(message = "Introduza um e-mail válido.")
    private String email;

    @NotBlank(message = "Introduza um número de telemóvel português válido.")
    @ValidTelefonePortugues(message = "Introduza um número de telemóvel português válido.")
    private String telemovel;

    /** Telefone fixo — opcional; se preenchido, validar como número PT fixo (começa por 2). */
    @Pattern(
        regexp  = "^$|^(\\+351|00351)?2\\d{8}$",
        message = "Introduza um telefone fixo português válido."
    )
    private String telefone;

    // ── Morada ────────────────────────────────────────────────────────────────

    @Size(min = 3, message = "Introduza uma rua válida.")
    private String rua;

    @Pattern(
        regexp  = "(?U)^$|^[\\p{L}\\d .\\-/º°ª,]+$",
        message = "Introduza um número de porta válido."
    )
    private String numeroPorta;

    @Pattern(
        regexp  = "^$|^\\d{4}-\\d{3}$",
        message = "Introduza um código postal válido no formato 1234-567."
    )
    private String codigoPostal;

    @Pattern(
        regexp  = "(?U)^$|^[\\p{L}]+([ \\-][\\p{L}]+)*$",
        message = "Introduza uma localidade válida."
    )
    private String localidade;

    // ── Campos apenas para visualização (não validados nem submetidos) ────────

    /** Mostrado no card lateral — não editável. */
    private String tipoUtilizador;

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public String getPrimeiroNome() { return primeiroNome; }
    public void setPrimeiroNome(String v) { this.primeiroNome = v != null ? v.trim() : null; }

    public String getUltimoNome() { return ultimoNome; }
    public void setUltimoNome(String v) { this.ultimoNome = v != null ? v.trim() : null; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate v) { this.dataNascimento = v; }

    public String getNif() { return nif; }
    public void setNif(String v) { this.nif = v != null ? v.trim() : null; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v != null ? v.trim() : null; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String v) { this.telemovel = v != null ? v.trim() : null; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String v) { this.telefone = v != null ? v.trim() : null; }

    public String getRua() { return rua; }
    public void setRua(String v) { this.rua = v != null ? v.trim() : null; }

    public String getNumeroPorta() { return numeroPorta; }
    public void setNumeroPorta(String v) { this.numeroPorta = v != null ? v.trim() : null; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String v) { this.codigoPostal = v != null ? v.trim() : null; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String v) { this.localidade = v != null ? v.trim() : null; }

    public String getTipoUtilizador() { return tipoUtilizador; }
    public void setTipoUtilizador(String v) { this.tipoUtilizador = v; }
}
