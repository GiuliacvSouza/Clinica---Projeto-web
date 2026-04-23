package bll;

import dal.PacienteRepository;
import model.Paciente;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;
    private final UtilizadorService utilizadorService;

    public PacienteService(PacienteRepository repository, UtilizadorService utilizadorService) {
        this.repository = repository;
        this.utilizadorService = utilizadorService;
    }

    @Transactional
    public Paciente salvar(Paciente paciente) {
        if (paciente.getUtilizador() == null) {
            throw new RuntimeException("Paciente deve estar associado a um utilizador.");
        }
        if (paciente.getStatus() == null || paciente.getStatus().isBlank()) {
            throw new RuntimeException("Status do paciente e obrigatorio.");
        }
        if (paciente.getDataRegisto() != null && paciente.getDataRegisto().isAfter(LocalDate.now())) {
            throw new RuntimeException("Data de registo nao pode ser futura.");
        }

        paciente.setUtilizador(utilizadorService.salvar(paciente.getUtilizador()));
        return repository.save(paciente);
    }

    public List<Paciente> listarTodos() {
        return repository.findAllComUtilizador();
    }

    public List<Paciente> pesquisarPorNomeOuNif(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }
        return repository.pesquisarPorNomeOuNif(termo.trim());
    }

    public Paciente buscarPorId(Integer id) {
        return repository.findByIdComUtilizador(id)
                .orElseThrow(() -> new RuntimeException("Paciente nao encontrado."));
    }

    public Paciente buscarPorNif(String nif) {
        return repository.findByNif(nif)
                .orElseThrow(() -> new RuntimeException("Paciente nao encontrado."));
    }

    public Paciente buscarPorNifOuNull(String nif) {
        if (nif == null || nif.isBlank()) {
            return null;
        }
        return repository.findByNif(nif).orElse(null);
    }

    public Paciente atualizarStatus(Integer id, String status) {
        Paciente paciente = buscarPorId(id);
        paciente.setStatus(status);
        return repository.save(paciente);
    }
}
