package bll;

import dal.PacienteRepository;
import model.Paciente;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public Paciente salvar(Paciente paciente) {

        if (paciente.getUtilizador() == null) {
            throw new RuntimeException("Paciente deve estar associado a um utilizador.");
        }

        if (paciente.getStatus() == null || paciente.getStatus().isBlank()) {
            throw new RuntimeException("Status do paciente é obrigatório.");
        }

        if (paciente.getDataRegisto() != null &&
                paciente.getDataRegisto().isAfter(LocalDate.now())) {

            throw new RuntimeException("Data de registo não pode ser futura.");
        }

        return repository.save(paciente);
    }

    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    public Paciente buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado."));
    }

    public Paciente atualizarStatus(Integer id, String status) {

        Paciente paciente = buscarPorId(id);

        paciente.setStatus(status);

        return repository.save(paciente);
    }
}