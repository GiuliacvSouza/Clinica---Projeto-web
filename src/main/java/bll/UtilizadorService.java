package bll;

import dal.UtilizadorRepository;
import model.Utilizador;
import org.springframework.beans.factory.annotation.Autowired; // Não esqueça o import!
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import do encoder
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilizadorService {

    private final UtilizadorRepository repository;
    private final BCryptPasswordEncoder encoder;

    // Melhor prática: Injeção via construtor (garante que o Spring forneça ambos)
    @Autowired
    public UtilizadorService(UtilizadorRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public Utilizador salvar(Utilizador utilizador) {
        // 1. Validações básicas antes de processar
        if (utilizador.getPrimeiroNome() == null || utilizador.getPrimeiroNome().isBlank()) {
            throw new RuntimeException("Primeiro nome é obrigatório.");
        }
        if (utilizador.getEmail() == null || utilizador.getEmail().isBlank()) {
            throw new RuntimeException("Email é obrigatório.");
        }
        if (utilizador.getSenha() == null || utilizador.getSenha().isBlank()) {
            throw new RuntimeException("Senha é obrigatória.");
        }

        // 2. Verifica duplicidade de email
        boolean duplicado = utilizador.getId() == null
                && repository.existsByEmail(utilizador.getEmail());

        if (duplicado) {
            throw new RuntimeException("Email já registado.");
        }

        // 3. CRIPTOGRAFIA (Só faz o hash se a senha não estiver criptografada ainda)
        // Dica: Verificamos se a senha começa com $2a$ (prefixo do BCrypt) para evitar re-criptografar
        if (!utilizador.getSenha().startsWith("$2a$")) {
            String senhaHash = encoder.encode(utilizador.getSenha());
            utilizador.setSenha(senhaHash);
        }

        return repository.save(utilizador);
    }

    public Utilizador autenticar(String email, String palavraPasseInserida){
        if (email == null || email.isBlank() || palavraPasseInserida == null || palavraPasseInserida.isBlank()) {
            return null;
        }
        
        try {
            Utilizador utilizador = repository.findByEmail(email).orElse(null);
            
            if (utilizador == null) {
                System.err.println("Utilizador não encontrado: " + email);
                return null;
            }
            
            if (encoder.matches(palavraPasseInserida, utilizador.getSenha())) {
                System.out.println("Login bem-sucedido para: " + email);
                return utilizador;
            } else {
                System.err.println("Senha incorreta para: " + email);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erro na autenticação: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Utilizador> listarTodos() {
        return repository.findAll();
    }

    public Utilizador buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado."));
    }

    public void excluir(Integer id) {
        Utilizador u = buscarPorId(id);
        repository.delete(u);
    }
}