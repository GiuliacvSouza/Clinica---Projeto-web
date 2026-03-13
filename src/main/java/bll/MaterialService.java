package bll;

import dal.MaterialRepository;
import model.Material;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository repository;

    public MaterialService(MaterialRepository repository) {
        this.repository = repository;
    }

    public Material salvar(Material material) {

        if (material.getNome() == null || material.getNome().isBlank()) {
            throw new RuntimeException("Nome do material é obrigatório.");
        }

        if (material.getIdFornecedor() == null) {
            throw new RuntimeException("Material deve possuir fornecedor.");
        }

        if (material.getQuantidadeMinima() != null && material.getQuantidadeMinima() < 0) {
            throw new RuntimeException("Quantidade mínima inválida.");
        }

        if (material.getValorUnitario() != null &&
                material.getValorUnitario().doubleValue() < 0) {
            throw new RuntimeException("Valor unitário inválido.");
        }

        return repository.save(material);
    }

    public List<Material> listarTodos() {
        return repository.findAll();
    }

    public Material buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado."));
    }

    public Material atualizarEstoque(Integer id, Integer novaQuantidade) {

        Material material = buscarPorId(id);

        material.setQuantidadeAtual(novaQuantidade);

        return repository.save(material);
    }
}