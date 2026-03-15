package bll;

import dal.ProntuarioRepository;
import model.Prontuario;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProntuarioService {

    private final ProntuarioRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public ProntuarioService(ProntuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Prontuario criarProntuario(Prontuario prontuario) {

        if (prontuario.getPaciente() == null) {
            throw new RuntimeException("Prontuário deve estar associado a um paciente.");
        }

        Integer pacienteId = prontuario.getPaciente().getId();

        if (repository.existsById(pacienteId)) {
            throw new RuntimeException("Este paciente já possui prontuário.");
        }

        prontuario.setPaciente(entityManager.merge(prontuario.getPaciente()));
        prontuario.setDatacriacao(LocalDate.now());

        return repository.save(prontuario);
    }

    public List<Prontuario> listarTodos() {
        return repository.findAll();
    }

    public Prontuario buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado."));
    }

    public Prontuario atualizar(Prontuario prontuario) {
        buscarPorId(prontuario.getId());
        prontuario.setUltimaAtualizacao(LocalDate.now());
        return repository.save(prontuario);
    }

    public void excluir(Integer id) {
        repository.delete(buscarPorId(id));
    }
}