package bll;

import dal.ProcedimentoRepository;
import model.Procedimento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcedimentoService {

    private final ProcedimentoRepository repository;

    public ProcedimentoService(ProcedimentoRepository repository) {
        this.repository = repository;
    }

    public Procedimento salvar(Procedimento procedimento) {

        if (procedimento.getNome() == null || procedimento.getNome().isBlank()) {
            throw new RuntimeException("Nome do procedimento é obrigatório.");
        }

        if (procedimento.getValor() != null &&
                procedimento.getValor().doubleValue() < 0) {

            throw new RuntimeException("Valor do procedimento inválido.");
        }

        if (procedimento.getDuracaoEstimada() != null &&
                procedimento.getDuracaoEstimada() < 0) {

            throw new RuntimeException("Duração estimada inválida.");
        }

        return repository.save(procedimento);
    }

    public List<Procedimento> listarTodos() {
        return repository.findAll();
    }

    public Procedimento buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado."));
    }
}