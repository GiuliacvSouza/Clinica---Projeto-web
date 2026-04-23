package bll;

import dal.ConsultaRepository;
import model.Consulta;
import model.Dentista;
import model.Paciente;
import model.Utilizador;
import model.dto.ConsultaAgendadaDTO;
import model.enums.EstadoConsulta;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;

    public ConsultaService(ConsultaRepository repository) {
        this.repository = repository;
    }

    public Consulta agendarConsulta(Consulta consulta) {

        if (consulta.getIdPaciente() == null) {
            throw new RuntimeException("Consulta deve ter um paciente.");
        }

        if (consulta.getIdDentista() == null) {
            throw new RuntimeException("Consulta deve ter um dentista.");
        }

        if (consulta.getDataHoraInicio().isBefore(Instant.now())) {
            throw new RuntimeException("Não é possível agendar consulta no passado.");
        }

        return repository.save(consulta);
    }

    public List<Consulta> listarTodas() {
        return repository.findAllEager();
    }

    public List<ConsultaAgendadaDTO> listarTodasAgendadas() {
        return repository.findAllEager().stream()
                .map(this::toConsultaAgendadaDTO)
                .collect(Collectors.toList());
    }

    public List<Consulta> listarPorStatus(EstadoConsulta status) {
        return repository.findByStatus(status);
    }

    public List<ConsultaAgendadaDTO> listarPorStatusAgendadas(EstadoConsulta status) {
        return repository.findByStatus(status).stream()
                .map(this::toConsultaAgendadaDTO)
                .collect(Collectors.toList());
    }

    public Consulta buscarPorId(Integer id) {
        return repository.findByIdEager(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    public Consulta atualizar(Consulta consulta) {

        buscarPorId(consulta.getId());

        return repository.save(consulta);
    }

    @Transactional
    public Consulta cancelar(Integer id) {
        Consulta consulta = buscarPorId(id);
        validarTransicao(consulta.getStatus(), EstadoConsulta.CANCELADA);
        consulta.setStatus(EstadoConsulta.CANCELADA);
        consulta.setDataCancelamento(LocalDate.now());
        consulta.setMotivoCancelamento("Cancelada pela agenda.");
        return repository.save(consulta);
    }

    @Transactional
    public Consulta reagendar(Integer id, Instant novaDataHoraInicio) {
        Consulta consulta = buscarPorId(id);
        validarTransicao(consulta.getStatus(), EstadoConsulta.AGENDADA);

        if (novaDataHoraInicio == null) {
            throw new RuntimeException("A nova data da consulta é obrigatória.");
        }

        if (novaDataHoraInicio.isBefore(Instant.now())) {
            throw new RuntimeException("Não é possível reagendar consulta para o passado.");
        }

        validarConflitoHorario(consulta, novaDataHoraInicio);

        consulta.setDataHoraInicio(novaDataHoraInicio);
        consulta.setDataMarcacao(LocalDate.now());
        consulta.setDataCancelamento(null);
        consulta.setMotivoCancelamento(null);
        return repository.save(consulta);
    }

    @Transactional
    public Consulta marcarChegada(Integer id) {
        return atualizarStatus(id, EstadoConsulta.EM_ESPERA);
    }

    @Transactional
    public Consulta confirmarConsulta(Integer id) {
        return atualizarStatus(id, EstadoConsulta.CONFIRMADA);
    }

    @Transactional
    public Consulta iniciarConsulta(Integer id) {
        return atualizarStatus(id, EstadoConsulta.EM_CONSULTA);
    }

    @Transactional
    public Consulta finalizarConsulta(Integer id) {
        return atualizarStatus(id, EstadoConsulta.CONCLUIDA);
    }

    @Transactional
    public Consulta atualizarStatus(Integer id, EstadoConsulta novoStatus) {
        Consulta consulta = buscarPorId(id);
        validarTransicao(consulta.getStatus(), novoStatus);
        consulta.setStatus(novoStatus);

        if (novoStatus != EstadoConsulta.CANCELADA) {
            consulta.setDataCancelamento(null);
            consulta.setMotivoCancelamento(null);
        }

        return repository.save(consulta);
    }

    private ConsultaAgendadaDTO toConsultaAgendadaDTO(Consulta consulta) {
        return new ConsultaAgendadaDTO(
                consulta.getId(),
                getNomePaciente(consulta.getIdPaciente()),
                getNomeDentista(consulta.getIdDentista()),
                consulta.getTipo(),
                consulta.getDataHoraInicio(),
                consulta.getStatus()
        );
    }

    private String getNomePaciente(Paciente paciente) {
        return formatarNome(paciente != null ? paciente.getUtilizador() : null);
    }

    private String getNomeDentista(Dentista dentista) {
        return formatarNome(dentista != null ? dentista.getUtilizador() : null);
    }

    private String formatarNome(Utilizador utilizador) {
        if (utilizador == null) {
            return null;
        }

        String primeiroNome = utilizador.getPrimeiroNome() != null ? utilizador.getPrimeiroNome().trim() : "";
        String ultimoNome = utilizador.getUltimoNome() != null ? utilizador.getUltimoNome().trim() : "";
        String nomeCompleto = (primeiroNome + " " + ultimoNome).trim();
        return nomeCompleto.isEmpty() ? null : nomeCompleto;
    }

    private void validarTransicao(EstadoConsulta statusAtual, EstadoConsulta novoStatus) {
        if (statusAtual == null || novoStatus == null) {
            throw new RuntimeException("Status da consulta inválido.");
        }

        if (statusAtual == novoStatus) {
            return;
        }

        boolean transicaoValida = switch (statusAtual) {
            case AGENDADA -> novoStatus == EstadoConsulta.AGENDADA
                    || novoStatus == EstadoConsulta.CONFIRMADA
                    || novoStatus == EstadoConsulta.CANCELADA;
            case CONFIRMADA -> novoStatus == EstadoConsulta.EM_ESPERA;
            case EM_ESPERA -> novoStatus == EstadoConsulta.EM_CONSULTA;
            case EM_CONSULTA -> novoStatus == EstadoConsulta.CONCLUIDA;
            case CONCLUIDA, CANCELADA, FALTA, PENDENTE, EM_ATENDIMENTO -> false;
        };

        if (!transicaoValida) {
            throw new RuntimeException("Transição de estado inválida: " + statusAtual + " -> " + novoStatus);
        }
    }

    private void validarConflitoHorario(Consulta consulta, Instant novaDataHoraInicio) {
        if (consulta.getIdDentista() == null || consulta.getIdDentista().getId() == null) {
            throw new RuntimeException("Consulta deve ter um dentista.");
        }

        try {
            boolean horarioOcupado = repository.findConflitoHorario(consulta.getIdDentista().getId(), novaDataHoraInicio).stream()
                    .anyMatch(conflito -> !conflito.getId().equals(consulta.getId()));

            if (horarioOcupado) {
                throw new RuntimeException("O dentista já possui consulta marcada para esse horário.");
            }
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Não foi possível validar o novo horário da consulta.", ex);
        }
    }
}
