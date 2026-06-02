package bll;

import dal.PasswordResetTokenRepository;
import dal.UtilizadorRepository;
import jakarta.transaction.Transactional;
import model.PasswordResetToken;
import model.Utilizador;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Lógica de negócio para o fluxo completo de recuperação de palavra-passe:
 *   1. iniciarRecuperacao() — gera token e envia e-mail.
 *   2. validarToken()       — verifica se o token existe, é válido e não foi usado.
 *   3. redefinirSenha()     — valida confirmação, encripta e guarda nova senha.
 */
@Service
public class RecuperacaoSenhaService {

    /** Duração do token em minutos. */
    private static final int EXPIRACAO_MINUTOS = 30;

    private final PasswordResetTokenRepository tokenRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder encoder;

    public RecuperacaoSenhaService(
            PasswordResetTokenRepository tokenRepository,
            UtilizadorRepository utilizadorRepository,
            EmailService emailService,
            BCryptPasswordEncoder encoder
    ) {
        this.tokenRepository   = tokenRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.emailService      = emailService;
        this.encoder           = encoder;
    }

    /**
     * Inicia o processo de recuperação.
     * Por segurança, não lança excepção se o e-mail não existir —
     * a resposta ao utilizador é sempre a mesma mensagem genérica.
     *
     * @param email E-mail introduzido pelo utilizador.
     */
    @Transactional
    public void iniciarRecuperacao(String email) {
        if (email == null || email.isBlank()) return;

        Optional<Utilizador> opt = utilizadorRepository
                .findByEmailIgnoreCase(email.trim());

        if (opt.isEmpty()) {
            // Não revelar se o e-mail existe — retornar silenciosamente
            return;
        }

        Utilizador utilizador = opt.get();

        // Invalidar tokens anteriores do mesmo utilizador
        tokenRepository.deleteByUtilizador(utilizador);

        // Criar novo token
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(UUID.randomUUID().toString());
        prt.setUtilizador(utilizador);
        prt.setDataCriacao(Instant.now());
        prt.setDataExpiracao(Instant.now().plus(EXPIRACAO_MINUTOS, ChronoUnit.MINUTES));
        prt.setUsado(false);
        tokenRepository.save(prt);

        // Enviar e-mail (pode lançar RuntimeException se o SMTP falhar)
        emailService.enviarRecuperacaoSenha(utilizador.getEmail(), prt.getToken());
    }

    /**
     * Verifica se um token é válido (existe, não expirou, não foi usado).
     *
     * @param token String do token.
     * @return Optional com o token se válido, ou empty se inválido/expirado.
     */
    public Optional<PasswordResetToken> validarToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return tokenRepository.findByToken(token)
                .filter(PasswordResetToken::isValido);
    }

    /**
     * Redefine a senha do utilizador associado ao token.
     *
     * @param token           Token de recuperação.
     * @param novaSenha       Nova palavra-passe em texto simples.
     * @param confirmarSenha  Confirmação — deve ser igual a novaSenha.
     * @throws IllegalArgumentException se as senhas não coincidem.
     * @throws IllegalStateException    se o token for inválido/expirado.
     */
    @Transactional
    public void redefinirSenha(String token, String novaSenha, String confirmarSenha) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException(
                        "O link de recuperação é inválido ou expirou."));

        if (!prt.isValido()) {
            throw new IllegalStateException("O link de recuperação é inválido ou expirou.");
        }
        if (!novaSenha.equals(confirmarSenha)) {
            throw new IllegalArgumentException("As palavras-passe não coincidem.");
        }

        Utilizador u = prt.getUtilizador();
        u.setSenha(encoder.encode(novaSenha));
        utilizadorRepository.save(u);

        // Marcar o token como usado — nunca pode ser reutilizado
        prt.setUsado(true);
        tokenRepository.save(prt);
    }
}
