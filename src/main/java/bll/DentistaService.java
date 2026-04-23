package bll;

import dal.DentistaRepository;
import model.Dentista;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class DentistaService {

    private final DentistaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public DentistaService(DentistaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Dentista salvar(Dentista dentista) {
        if (dentista.getUtilizador() == null)
            throw new RuntimeException("Dentista deve estar associado a um utilizador.");
        if (dentista.getNumeroOmd() == null || dentista.getNumeroOmd().isBlank())
            throw new RuntimeException("Número OMD é obrigatório.");
        if (dentista.getDataAdmissao() != null && dentista.getDataAdmissao().isAfter(LocalDate.now()))
            throw new RuntimeException("Data de admissão não pode ser futura.");
        if (dentista.getHorarioEntrada() != null && dentista.getHorarioSaida() != null
                && dentista.getHorarioSaida().isBefore(dentista.getHorarioEntrada()))
            throw new RuntimeException("Horário de saída deve ser após horário de entrada.");

        dentista.setUtilizador(entityManager.merge(dentista.getUtilizador())); //  
        return repository.save(dentista);
    }

    public List<Dentista> listarTodos() { return repository.findAllComUtilizador(); }

    public Dentista buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado."));
    }

    public Dentista desativar(Integer id) {
        Dentista dentista = buscarPorId(id);
        dentista.setAtivo(false);
        return repository.save(dentista);
    }
}
