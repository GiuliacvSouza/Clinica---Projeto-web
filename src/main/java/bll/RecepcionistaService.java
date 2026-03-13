package bll;

import dal.RecepcionistaRepository;
import model.Recepcionista;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecepcionistaService {

    private final RecepcionistaRepository repository;

    public RecepcionistaService(RecepcionistaRepository repository) {
        this.repository = repository;
    }

    public Recepcionista salvar(Recepcionista recepcionista) {

        if (recepcionista.getUtilizador() == null) {
            throw new RuntimeException("Recepcionista deve estar associado a um utilizador.");
        }

        if (recepcionista.getDataAdmissao() != null &&
                recepcionista.getDataAdmissao().isAfter(LocalDate.now())) {

            throw new RuntimeException("Data de admissão não pode ser futura.");
        }

        recepcionista.setId(recepcionista.getUtilizador().getId());

        return repository.save(recepcionista);
    }

    public List<Recepcionista> listarTodos() {
        return repository.findAll();
    }

    public Recepcionista buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado."));
    }

    public void excluir(Integer id) {

        Recepcionista r = buscarPorId(id);

        repository.delete(r);
    }
}