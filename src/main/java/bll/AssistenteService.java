package bll;

import dal.AssistenteRepository;
import model.Assistente;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssistenteService {

    private final AssistenteRepository repository;

    public AssistenteService(AssistenteRepository repository) {
        this.repository = repository;
    }

    public Assistente salvar(Assistente assistente) {

        if (assistente.getUtilizador() == null) {
            throw new RuntimeException("Assistente deve estar associado a um utilizador.");
        }

        if (assistente.getDataAdmissao() != null &&
                assistente.getDataAdmissao().isAfter(LocalDate.now())) {

            throw new RuntimeException("Data de admissão não pode ser futura.");
        }

        return repository.save(assistente);
    }

    public List<Assistente> listarTodos() {
        return repository.findAll();
    }

    public Assistente buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assistente não encontrado"));
    }

    public void desativar(Integer id) {

        Assistente assistente = buscarPorId(id);

        assistente.setAtivo(false);

        repository.save(assistente);
    }
}