package bll;

import dal.FornecedorRepository;
import model.Fornecedor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    public FornecedorService(FornecedorRepository repository) {
        this.repository = repository;
    }

    public Fornecedor salvar(Fornecedor fornecedor) {

        if (fornecedor.getNome() == null || fornecedor.getNome().isBlank()) {
            throw new RuntimeException("Nome do fornecedor é obrigatório.");
        }

        if (fornecedor.getEmail() != null) {

            boolean duplicado = repository.findAll().stream()
                    .anyMatch(f ->
                            f.getEmail() != null &&
                                    f.getEmail().equalsIgnoreCase(fornecedor.getEmail()) &&
                                    !f.getId().equals(fornecedor.getId())
                    );

            if (duplicado) {
                throw new RuntimeException("Já existe fornecedor com este email.");
            }
        }

        return repository.save(fornecedor);
    }

    public List<Fornecedor> listarTodos() {
        return repository.findAll();
    }

    public Fornecedor buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado."));
    }

    public void excluir(Integer id) {

        Fornecedor fornecedor = buscarPorId(id);

        repository.delete(fornecedor);
    }
}