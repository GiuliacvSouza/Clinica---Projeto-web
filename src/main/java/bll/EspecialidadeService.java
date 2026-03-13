package bll;

import dal.EspecialidadeRepository;
import model.Especialidade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository repository;

    public EspecialidadeService(EspecialidadeRepository repository) {
        this.repository = repository;
    }

    public Especialidade salvar(Especialidade especialidade) {

        if (especialidade.getNome() == null || especialidade.getNome().isBlank()) {
            throw new RuntimeException("Nome da especialidade é obrigatório.");
        }

        boolean duplicado = repository.findAll().stream()
                .anyMatch(e ->
                        e.getNome().equalsIgnoreCase(especialidade.getNome())
                                && !e.getId().equals(especialidade.getId())
                );

        if (duplicado) {
            throw new RuntimeException("Especialidade já cadastrada.");
        }

        return repository.save(especialidade);
    }

    public List<Especialidade> listarTodas() {
        return repository.findAll();
    }

    public Especialidade buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada."));
    }

    public void excluir(Integer id) {

        Especialidade especialidade = buscarPorId(id);

        repository.delete(especialidade);
    }
}