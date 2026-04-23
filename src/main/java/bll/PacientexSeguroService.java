package bll;

import dal.PacientexSeguroRepository;
import model.PacientexSeguro;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacientexSeguroService {

    private final PacientexSeguroRepository repository;

    public PacientexSeguroService(PacientexSeguroRepository repository) {
        this.repository = repository;
    }

    public PacientexSeguro salvar(PacientexSeguro ps) {

        if (ps.getIdUtilizador() == null) {
            throw new RuntimeException("Paciente é obrigatório.");
        }

        if (ps.getIdSeguro() == null) {
            throw new RuntimeException("Seguro é obrigatório.");
        }

        if (ps.getDataInicioCobertura() != null &&
                ps.getDataFimCobertura() != null &&
                ps.getDataFimCobertura().isBefore(ps.getDataInicioCobertura())) {

            throw new RuntimeException("Data fim da cobertura não pode ser anterior à data de início.");
        }

        return repository.save(ps);
    }

    public List<PacientexSeguro> listarTodos() {
        return repository.findAllComRelacionamentos();
    }

    public PacientexSeguro buscarPorId(model.PacientexSeguroId id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro Paciente x Seguro não encontrado."));
    }

    public void excluir(model.PacientexSeguroId id) {

        PacientexSeguro ps = buscarPorId(id);

        repository.delete(ps);
    }
}
