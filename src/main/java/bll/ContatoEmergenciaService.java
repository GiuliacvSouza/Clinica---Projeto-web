package bll;

import dal.ContatoEmergenciaRepository;
import model.ContatoEmergencia;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContatoEmergenciaService {

    private final ContatoEmergenciaRepository repository;

    public ContatoEmergenciaService(ContatoEmergenciaRepository repository) {
        this.repository = repository;
    }

    public ContatoEmergencia salvar(ContatoEmergencia contato) {

        if (contato.getPaciente() == null) {
            throw new RuntimeException("Contato de emergência deve estar associado a um paciente.");
        }

        if (contato.getPrimeiroNome() == null || contato.getPrimeiroNome().isBlank()) {
            throw new RuntimeException("Primeiro nome é obrigatório.");
        }

        return repository.save(contato);
    }

    public List<ContatoEmergencia> listarTodos() {
        return repository.findAll();
    }

    public ContatoEmergencia buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato de emergência não encontrado."));
    }

    public void excluir(Integer id) {

        ContatoEmergencia contato = buscarPorId(id);

        repository.delete(contato);
    }
}