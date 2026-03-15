package bll;

import dal.UtilizadorRepository;
import model.Utilizador;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilizadorService {

    private final UtilizadorRepository repository;

    public UtilizadorService(UtilizadorRepository repository) {
        this.repository = repository;
    }

    public Utilizador salvar(Utilizador utilizador) {

        if (utilizador.getPrimeiroNome() == null || utilizador.getPrimeiroNome().isBlank()) {
            throw new RuntimeException("Primeiro nome é obrigatório.");
        }

        if (utilizador.getEmail() == null || utilizador.getEmail().isBlank()) {
            throw new RuntimeException("Email é obrigatório.");
        }

        boolean duplicado = utilizador.getId() == null
                && repository.existsByEmail(utilizador.getEmail());

        if (duplicado) {
            throw new RuntimeException("Email já cadastrado.");
        }

        if (utilizador.getSenha() == null || utilizador.getSenha().isBlank()) {
            throw new RuntimeException("Senha é obrigatória.");
        }

        return repository.save(utilizador);
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