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
@Service
public class RecuperacaoSenhaService {
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
@Transactional
    public void iniciarRecuperacao(String email) {
        if (email == null || email.isBlank()) return;

        Optional<Utilizador> opt = utilizadorRepository
                .findByEmailIgnoreCase(email.trim());

        if (opt.isEmpty()) {
            return;
        }

        Utilizador utilizador = opt.get();

        tokenRepository.deleteByUtilizador(utilizador);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(UUID.randomUUID().toString());
        prt.setUtilizador(utilizador);
        prt.setDataCriacao(Instant.now());
        prt.setDataExpiracao(Instant.now().plus(EXPIRACAO_MINUTOS, ChronoUnit.MINUTES));
        prt.setUsado(false);
        tokenRepository.save(prt);

        emailService.enviarRecuperacaoSenha(utilizador.getEmail(), prt.getToken());
    }
public Optional<PasswordResetToken> validarToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return tokenRepository.findByToken(token)
                .filter(PasswordResetToken::isValido);
    }
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

        prt.setUsado(true);
        tokenRepository.save(prt);
    }
}
