package bll;

import dal.RecepcionistaRepository;
import dal.UtilizadorRepository;
import model.Recepcionista;
import model.Utilizador;
import model.enums.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecepcionistaService {

    private final RecepcionistaRepository recepcionistaRepository;
    private final UtilizadorRepository utilizadorRepository;

    @Autowired
    public RecepcionistaService(RecepcionistaRepository recepcionistaRepository,
                                UtilizadorRepository utilizadorRepository) {
        this.recepcionistaRepository = recepcionistaRepository;
        this.utilizadorRepository = utilizadorRepository;
    }

    @Transactional
    public Recepcionista salvar(Recepcionista recepcionista) {
        if (recepcionista.getUtilizador() == null) {
            throw new RuntimeException("Recepcionista deve estar associado a um utilizador.");
        }

        if (recepcionista.getDataAdmissao() != null &&
                recepcionista.getDataAdmissao().isAfter(LocalDate.now())) {
            throw new RuntimeException("Data de admissão não pode ser futura.");
        }

        Utilizador utilizador = utilizadorRepository.findById(recepcionista.getUtilizador().getId())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado."));

        utilizador.setTipoUtilizador("RECEPCIONISTA");
        utilizadorRepository.save(utilizador);

        recepcionista.setUtilizador(utilizador);

        if (recepcionista.getDataAdmissao() == null) {
            recepcionista.setDataAdmissao(LocalDate.now());
        }

        if (recepcionista.getTurno() == null) {
            recepcionista.setTurno(Turno.MANHA);
        }

        return recepcionistaRepository.save(recepcionista);
    }

    public List<Recepcionista> listarTodos() {
        return recepcionistaRepository.findAll();
    }

    public Recepcionista buscarPorId(Integer id) {
        return recepcionistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado com ID: " + id));
    }

    public Recepcionista buscarPorUtilizadorId(Integer utilizadorId) {
        return recepcionistaRepository.findByUtilizadorId(utilizadorId)
                .orElseThrow(() -> new RuntimeException("Recepcionista não encontrado para o utilizador: " + utilizadorId));
    }

    public List<Recepcionista> buscarPorTurno(Turno turno) {
        return recepcionistaRepository.findByTurno(turno);
    }

    @Transactional
    public void excluir(Integer id) {
        Recepcionista recepcionista = buscarPorId(id);

        Utilizador utilizador = recepcionista.getUtilizador();
        utilizador.setTipoUtilizador(null);
        utilizadorRepository.save(utilizador);

        recepcionistaRepository.delete(recepcionista);
    }

    @Transactional
    public Recepcionista atualizarTurno(Integer id, Turno novoTurno) {
        Recepcionista recepcionista = buscarPorId(id);
        recepcionista.setTurno(novoTurno);
        return recepcionistaRepository.save(recepcionista);
    }
}