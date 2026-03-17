package bll;

import dal.RecepcionistaRepository;
import model.Recepcionista;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecepcionistaService {

    private final RecepcionistaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public RecepcionistaService(RecepcionistaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Recepcionista salvar(Recepcionista recepcionista) {
        if (recepcionista.getUtilizador() == null)
            throw new RuntimeException("Recepcionista deve estar associado a um utilizador.");
        if (recepcionista.getDataAdmissao() != null && recepcionista.getDataAdmissao().isAfter(LocalDate.now()))
            throw new RuntimeException("Data de admissão não pode ser futura.");

        // REMOVIDO: recepcionista.setId(recepcionista.getUtilizador().getId());
        recepcionista.setUtilizador(entityManager.merge(recepcionista.getUtilizador())); //  
        return repository.save(recepcionista);
    }

    public List<Recepcionista> listarTodos() { return repository.findAll(); }

    public Recepcionista buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado."));
    }

    public void excluir(Integer id) {
        repository.delete(buscarPorId(id));
    }
}