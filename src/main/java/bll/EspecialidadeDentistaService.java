package bll;

import dal.EspecialidadeDentistaRepository;
import model.EspecialidadeDentista;
import model.EspecialidadeDentistaId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadeDentistaService {

    private final EspecialidadeDentistaRepository repository;

    public EspecialidadeDentistaService(EspecialidadeDentistaRepository repository) {
        this.repository = repository;
    }

    public EspecialidadeDentista salvar(EspecialidadeDentista ed) {

        if (ed.getIdUtilizador() == null) {
            throw new RuntimeException("Dentista é obrigatório.");
        }

        if (ed.getIdEspecialidade() == null) {
            throw new RuntimeException("Especialidade é obrigatória.");
        }

        if (repository.existsById(ed.getId())) {
            throw new RuntimeException("Este dentista já possui essa especialidade.");
        }

        return repository.save(ed);
    }

    public List<EspecialidadeDentista> listarTodos() {
        return repository.findAll();
    }

    public EspecialidadeDentista buscarPorId(EspecialidadeDentistaId id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("EspecialidadeDentista não encontrada."));
    }

    public void excluir(EspecialidadeDentistaId id) {

        EspecialidadeDentista ed = buscarPorId(id);

        repository.delete(ed);
    }
}