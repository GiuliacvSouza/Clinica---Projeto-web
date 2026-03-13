package bll;

import dal.MaterialRepository;
import dal.MovimentacaoEstoqueRepository;
import model.Material;
import model.MovimentacaoEstoque;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository repository;
    private final MaterialRepository materialRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository repository,
            MaterialRepository materialRepository) {

        this.repository = repository;
        this.materialRepository = materialRepository;
    }

    public MovimentacaoEstoque registrarMovimentacao(MovimentacaoEstoque mov) {

        if (mov.getIdMaterial() == null) {
            throw new RuntimeException("Movimentação deve possuir material.");
        }

        if (mov.getQuantidade() == null || mov.getQuantidade() == 0) {
            throw new RuntimeException("Quantidade inválida.");
        }

        Material material = materialRepository
                .findById(mov.getIdMaterial().getId())
                .orElseThrow(() -> new RuntimeException("Material não encontrado."));

        int novoEstoque = material.getQuantidadeAtual() + mov.getQuantidade();

        if (novoEstoque < 0) {
            throw new RuntimeException("Estoque insuficiente.");
        }

        material.setQuantidadeAtual(novoEstoque);

        materialRepository.save(material);

        mov.setData(LocalDate.now());

        return repository.save(mov);
    }

    public List<MovimentacaoEstoque> listarTodos() {
        return repository.findAll();
    }

}