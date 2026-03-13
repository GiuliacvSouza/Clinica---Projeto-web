package bll;

import dal.SeguroRepository;
import model.Seguro;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SeguroService {

    private final SeguroRepository repository;

    public SeguroService(SeguroRepository repository) {
        this.repository = repository;
    }

    public Seguro salvar(Seguro seguro) {

        if (seguro.getNomeSeguro() == null || seguro.getNomeSeguro().isBlank()) {
            throw new RuntimeException("Nome do seguro é obrigatório.");
        }

        if (seguro.getValidoAte() != null &&
                seguro.getValidoAte().isBefore(LocalDate.now())) {

            throw new RuntimeException("Seguro já expirado.");
        }

        return repository.save(seguro);
    }

    public List<Seguro> listarTodos() {
        return repository.findAll();
    }

    public Seguro buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado."));
    }

    public void excluir(Integer id) {

        Seguro seguro = buscarPorId(id);

        repository.delete(seguro);
    }
}