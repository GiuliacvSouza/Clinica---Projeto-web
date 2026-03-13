package bll;

import dal.DoencaRepository;
import model.Doenca;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoencaService {

    private final DoencaRepository repository;

    public DoencaService(DoencaRepository repository) {
        this.repository = repository;
    }

    public Doenca salvar(Doenca doenca) {

        if (doenca.getNome() == null || doenca.getNome().isBlank()) {
            throw new RuntimeException("Nome da doença é obrigatório.");
        }

        boolean duplicado = repository.findAll().stream()
                .anyMatch(d ->
                        d.getNome().equalsIgnoreCase(doenca.getNome())
                                && !d.getId().equals(doenca.getId())
                );

        if (duplicado) {
            throw new RuntimeException("Doença já cadastrada.");
        }

        return repository.save(doenca);
    }

    public List<Doenca> listarTodas() {
        return repository.findAll();
    }

    public Doenca buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doença não encontrada."));
    }

    public Doenca desativar(Integer id) {

        Doenca doenca = buscarPorId(id);

        doenca.setAtiva(false);

        return repository.save(doenca);
    }
}