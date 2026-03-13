package bll;

import dal.EspecialidadexAssistenteRepository;
import model.EspecialidadexAssistente;
import model.EspecialidadexAssistenteId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadexAssistenteService {

    private final EspecialidadexAssistenteRepository repository;

    public EspecialidadexAssistenteService(EspecialidadexAssistenteRepository repository) {
        this.repository = repository;
    }

    public EspecialidadexAssistente salvar(EspecialidadexAssistente ea) {

        if (ea.getIdUtilizador() == null) {
            throw new RuntimeException("Assistente é obrigatório.");
        }

        if (ea.getIdEspecialidade() == null) {
            throw new RuntimeException("Especialidade é obrigatória.");
        }

        if (repository.existsById(ea.getId())) {
            throw new RuntimeException("Assistente já possui essa especialidade.");
        }

        return repository.save(ea);
    }

    public List<EspecialidadexAssistente> listarTodos() {
        return repository.findAll();
    }

    public EspecialidadexAssistente buscarPorId(EspecialidadexAssistenteId id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado."));
    }

    public void excluir(EspecialidadexAssistenteId id) {
        repository.deleteById(id);
    }
}