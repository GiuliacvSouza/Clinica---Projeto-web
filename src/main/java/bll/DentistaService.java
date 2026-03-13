package bll;

import dal.DentistaRepository;
import model.Dentista;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DentistaService {

    private final DentistaRepository repository;

    public DentistaService(DentistaRepository repository) {
        this.repository = repository;
    }

    public Dentista salvar(Dentista dentista) {

        if (dentista.getUtilizador() == null) {
            throw new RuntimeException("Dentista deve estar associado a um utilizador.");
        }

        if (dentista.getNumeroOmd() == null || dentista.getNumeroOmd().isBlank()) {
            throw new RuntimeException("Número OMD é obrigatório.");
        }

        if (dentista.getDataAdmissao() != null &&
                dentista.getDataAdmissao().isAfter(LocalDate.now())) {

            throw new RuntimeException("Data de admissão não pode ser futura.");
        }

        if (dentista.getHorarioEntrada() != null &&
                dentista.getHorarioSaida() != null &&
                dentista.getHorarioSaida().isBefore(dentista.getHorarioEntrada())) {

            throw new RuntimeException("Horário de saída deve ser após horário de entrada.");
        }

        return repository.save(dentista);
    }

    public List<Dentista> listarTodos() {
        return repository.findAll();
    }

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