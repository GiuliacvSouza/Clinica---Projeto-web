package model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Token temporário para recuperação de palavra-passe.
 *
 * Ciclo de vida:
 *   1. Criado quando o utilizador pede recuperação.
 *   2. Enviado por e-mail como parâmetro de URL.
 *   3. Verificado em /redefinir-senha: não expirado + não usado.
 *   4. Marcado como usado após redefinição bem-sucedida.
 */
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token UUID único — usado como parâmetro no link de recuperação. */
    @Column(nullable = false, unique = true, length = 255)
    private String token;

    /** Utilizador ao qual o token pertence. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_utilizador", nullable = false)
    private Utilizador utilizador;

    @Column(name = "data_criacao", nullable = false)
    private Instant dataCriacao;

    /** O token expira 30 minutos após a criação. */
    @Column(name = "data_expiracao", nullable = false)
    private Instant dataExpiracao;

    /** Após utilização bem-sucedida é marcado true — não pode ser reutilizado. */
    @Column(nullable = false)
    private boolean usado = false;

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Utilizador getUtilizador() { return utilizador; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }

    public Instant getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Instant dataCriacao) { this.dataCriacao = dataCriacao; }

    public Instant getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(Instant dataExpiracao) { this.dataExpiracao = dataExpiracao; }

    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }

    // ── Métodos de conveniência ───────────────────────────────────────────────

    /** Devolve true se o token já expirou. */
    public boolean isExpirado() {
        return Instant.now().isAfter(dataExpiracao);
    }

    /** Devolve true se o token ainda é válido (não expirado e não usado). */
    public boolean isValido() {
        return !usado && !isExpirado();
    }
}
