package bll;

import dal.MedicamentoRepository;
import model.Medicamento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository repository;

    public MedicamentoService(MedicamentoRepository repository) {
        this.repository = repository;
    }

    public Medicamento salvar(Medicamento medicamento) {

        if (medicamento.getNome() == null || medicamento.getNome().isBlank()) {
            throw new RuntimeException("Nome do medicamento é obrigatório.");
        }

        boolean duplicado = repository.findAll().stream()
                .anyMatch(m ->
                        m.getNome().equalsIgnoreCase(medicamento.getNome())
                                && !m.getId().equals(medicamento.getId())
                );

        if (duplicado) {
            throw new RuntimeException("Medicamento já cadastrado.");
        }

        return repository.save(medicamento);
    }

    public List<Medicamento> listarTodos() {
        return repository.findAll();
    }

    public Medicamento buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento não encontrado."));
    }

    public void excluir(Integer id) {

        Medicamento medicamento = buscarPorId(id);

        repository.delete(medicamento);
    }
}