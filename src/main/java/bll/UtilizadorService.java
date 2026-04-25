package bll;

import dal.UtilizadorRepository;
import model.Utilizador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilizadorService {

    private final UtilizadorRepository repository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UtilizadorService(UtilizadorRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Utilizador salvar(Utilizador utilizador) {
        if (utilizador.getPrimeiroNome() == null || utilizador.getPrimeiroNome().isBlank()) {
            throw new RuntimeException("Primeiro nome e obrigatorio.");
        }
        if (utilizador.getEmail() == null || utilizador.getEmail().isBlank()) {
            throw new RuntimeException("Email e obrigatorio.");
        }

        utilizador.setPrimeiroNome(utilizador.getPrimeiroNome().trim());
        if (utilizador.getUltimoNome() != null) {
            utilizador.setUltimoNome(utilizador.getUltimoNome().trim());
        }

        Integer utilizadorId = utilizador.getId();
        String email = utilizador.getEmail().trim().toLowerCase();
        utilizador.setEmail(email);

        if (utilizador.getNif() != null && !utilizador.getNif().isBlank()) {
            String nif = utilizador.getNif().trim();
            if (!nif.matches("\\d{9}")) {
                throw new RuntimeException("NIF deve conter exatamente 9 digitos.");
            }

            boolean nifDuplicado = utilizadorId == null
                    ? repository.existsByNif(nif)
                    : repository.existsByNifAndIdNot(nif, utilizadorId);
            if (nifDuplicado) {
                throw new RuntimeException("NIF ja registado.");
            }
            utilizador.setNif(nif);
        }

        boolean emailDuplicado = utilizadorId == null
                ? repository.existsByEmail(email)
                : repository.existsByEmailAndIdNot(email, utilizadorId);
        if (emailDuplicado) {
            throw new RuntimeException("Email ja registado.");
        }

        if (utilizador.getSenha() == null || utilizador.getSenha().isBlank()) {
            if (utilizadorId == null) {
                throw new RuntimeException("Senha e obrigatoria.");
            }

            String senhaExistente = repository.findById(utilizadorId)
                    .map(Utilizador::getSenha)
                    .orElseThrow(() -> new RuntimeException("Utilizador nao encontrado."));
            utilizador.setSenha(senhaExistente);
        }

        if (!utilizador.getSenha().startsWith("$2a$")
                && !utilizador.getSenha().startsWith("$2b$")
                && !utilizador.getSenha().startsWith("$2y$")) {
            utilizador.setSenha(encoder.encode(utilizador.getSenha()));
        }

        return repository.save(utilizador);
    }

    public Utilizador autenticar(String email, String palavraPasseInserida) {
        if (email == null || email.isBlank() || palavraPasseInserida == null || palavraPasseInserida.isBlank()) {
            return null;
        }
        // normalizar email para corresponder ao formato guardado (trim + lower-case)
        String emailNorm = email.trim().toLowerCase();

        try {
            Utilizador utilizador = null;

            System.err.println("[AUTH DEBUG] autenticar() - raw='" + email + "' norm='" + emailNorm + "'");
            try { System.err.println("[AUTH DEBUG] existsByEmail(raw norm)=" + repository.existsByEmail(emailNorm)); } catch (Exception ignored) {}

            // tentativa direta
            try { utilizador = repository.findByEmail(emailNorm).orElse(null); } catch (Exception ignored) { utilizador = null; }
            if (utilizador != null) {
                System.err.println("[AUTH DEBUG] encontrado via findByEmail");
            } else {
                // tentativa fallback: pesquisa case-insensitive
                try { utilizador = repository.findByEmailIgnoreCase(emailNorm).orElse(null); } catch (Exception ignored) { utilizador = null; }
                if (utilizador != null) {
                    System.err.println("[AUTH DEBUG] encontrado via findByEmailIgnoreCase");
                } else {
                    // tentativa final: usar query normalizada (TRIM + LOWER) para cobrir spaces invisiveis
                    try { utilizador = repository.findByEmailNormalized(emailNorm).orElse(null); } catch (Exception ignored) { utilizador = null; }
                    if (utilizador != null) {
                        System.err.println("[AUTH DEBUG] encontrado via findByEmailNormalized");
                    }
                }
            }

            if (utilizador == null) {
                System.err.println("Utilizador nao encontrado: " + emailNorm);
                // busca candidatos semelhantes para ajudar a diagnosticar
                try {
                    String localPart = emailNorm.contains("@") ? emailNorm.substring(0, emailNorm.indexOf('@')) : emailNorm;
                    System.err.println("[AUTH DEBUG] procurando candidatos contendo: '" + localPart + "'");
                    var candidatos = repository.findByEmailContainingIgnoreCase(localPart);
                    System.err.println("[AUTH DEBUG] candidatos encontrados: " + (candidatos == null ? 0 : candidatos.size()));
                    if (candidatos != null) {
                        for (Utilizador c : candidatos) {
                            System.err.println("[AUTH DEBUG] candidato.email='" + c.getEmail() + "'");
                        }
                    }
                } catch (Exception ignored) {}

                return null;
            }

            if (encoder.matches(palavraPasseInserida, utilizador.getSenha())) {
                System.out.println("Login bem-sucedido para: " + emailNorm);
                return utilizador;
            }

            System.err.println("Senha incorreta para: " + emailNorm);
            return null;
        } catch (Exception e) {
            System.err.println("Erro na autenticacao: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Utilizador> listarTodos() {
        return repository.findAll();
    }

    public Utilizador buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador nao encontrado."));
    }

    public void excluir(Integer id) {
        Utilizador utilizador = buscarPorId(id);
        repository.delete(utilizador);
    }
}
