package bll;

import dal.ConsultaRepository;
import dal.DentistaRepository;
import dal.PacienteRepository;
import dal.ProcedimentoRepository;
import dal.AtendimentoProcedimentoRepository;
import bll.AtendimentoService;
import jakarta.transaction.Transactional;
import model.Consulta;
import model.Dentista;
import model.Paciente;
import model.Utilizador;
import model.dto.ConsultaAgendadaDTO;
import model.enums.EstadoConsulta;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;
    private final AtendimentoService atendimentoService;
    private final ProcedimentoRepository procedimentoRepository;
    private final AtendimentoProcedimentoRepository atendimentoProcedimentoRepository;

    public ConsultaService(
            ConsultaRepository repository,
            PacienteRepository pacienteRepository,
            DentistaRepository dentistaRepository,
            AtendimentoService atendimentoService,
            ProcedimentoRepository procedimentoRepository,
            AtendimentoProcedimentoRepository atendimentoProcedimentoRepository
    ) {
        this.repository = repository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
        this.atendimentoService = atendimentoService;
        this.procedimentoRepository = procedimentoRepository;
        this.atendimentoProcedimentoRepository = atendimentoProcedimentoRepository;
    }

    private static final int HORAS_MINIMAS_CANCELAMENTO = 24;
    private static final int DURACAO_PADRAO_MINUTOS = 45;
    private static final ZoneId ZONA_HORARIA = ZoneId.systemDefault();
    private static final List<LocalTime> HORARIOS_BASE = List.of(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            LocalTime.of(11, 0),
            LocalTime.of(11, 30),
            LocalTime.of(14, 0),
            LocalTime.of(14, 30),
            LocalTime.of(15, 0),
            LocalTime.of(15, 30),
            LocalTime.of(16, 0),
            LocalTime.of(16, 30)
    );

    @Transactional
    public Consulta agendarConsulta(Consulta consulta) {
        if (consulta.getIdPaciente() == null || consulta.getIdPaciente().getId() == null) {
            throw new RuntimeException("Consulta deve ter um paciente.");
        }
        if (consulta.getIdDentista() == null || consulta.getIdDentista().getId() == null) {
            throw new RuntimeException("Consulta deve ter um dentista.");
        }
        if (consulta.getDataHoraInicio() == null || consulta.getDataHoraInicio().isBefore(Instant.now())) {
            throw new RuntimeException("Nao e possivel agendar consulta no passado.");
        }

        Paciente paciente = pacienteRepository.findById(consulta.getIdPaciente().getId())
                .orElseThrow(() -> new RuntimeException("Paciente nao encontrado."));
        Dentista dentista = dentistaRepository.findById(consulta.getIdDentista().getId())
                .orElseThrow(() -> new RuntimeException("Dentista nao encontrado."));

        consulta.setIdPaciente(paciente);
        consulta.setIdDentista(dentista);
        validarConflitoHorario(consulta, consulta.getDataHoraInicio());

        Consulta consultaGuardada = repository.saveAndFlush(consulta);
        if (consultaGuardada.getId() == null) {
            throw new RuntimeException("Nao foi possivel guardar a consulta.");
        }

        return consultaGuardada;
    }

    public List<Consulta> listarTodas() {
        return repository.findAllEager();
    }

    public List<ConsultaAgendadaDTO> listarTodasAgendadas() {
        try {
            List<ConsultaAgendadaDTO> consultas = repository.findAllAgendadas();
            if (!consultas.isEmpty() || repository.count() == 0) {
                return consultas;
            }
        } catch (Exception ignored) {
            // Mantem a agenda visivel se a projecao DTO falhar.
        }

        return repository.findAllEager().stream()
                .sorted(Comparator.comparing(Consulta::getDataHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toConsultaAgendadaDTO)
                .collect(Collectors.toList());
    }

    public List<Consulta> listarPorStatus(EstadoConsulta status) {
        return repository.findByStatus(status);
    }

    public List<Consulta> listarPorPaciente(Integer pacienteId) {
        if (pacienteId == null) {
            return List.of();
        }
        return repository.findByPacienteIdEager(pacienteId);
    }

    public List<ConsultaAgendadaDTO> listarPorStatusAgendadas(EstadoConsulta status) {
        try {
            List<ConsultaAgendadaDTO> consultas = repository.findByStatusAgendadas(status);
            if (!consultas.isEmpty() || repository.findByStatus(status).isEmpty()) {
                return consultas;
            }
        } catch (Exception ignored) {
            // Mantem a agenda visivel se a projecao DTO falhar.
        }

        return repository.findByStatus(status).stream()
                .sorted(Comparator.comparing(Consulta::getDataHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toConsultaAgendadaDTO)
                .collect(Collectors.toList());
    }

    public List<ConsultaAgendadaDTO> filtrarConsultasAgendadas(
            EstadoConsulta status,
            Integer dentistaId,
            Integer pacienteId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String periodo,
            String pesquisa,
            String tipo
    ) {
        PeriodoInstants periodoInstants = resolverPeriodo(periodo);
        Instant inicio = dataInicio != null
                ? dataInicio.atStartOfDay(ZONA_HORARIA).toInstant()
                : periodoInstants.inicio();
        Instant fim = dataFim != null
                ? dataFim.plusDays(1).atStartOfDay(ZONA_HORARIA).minusNanos(1).toInstant()
                : periodoInstants.fim();
        String tipoNormalizado = normalizarTexto(tipo);
        String pesquisaNormalizada = normalizarPesquisa(pesquisa);

        return repository.filtrarConsultasAgendadas(
                status,
                dentistaId,
                pacienteId,
                inicio != null,
                inicio,
                fim != null,
                fim,
                tipoNormalizado != null,
                tipoNormalizado,
                pesquisaNormalizada != null,
                pesquisaNormalizada
        );
    }

    public List<String> listarTiposConsulta() {
        return repository.findTiposConsulta();
    }

    public List<Consulta> listarPorDentistaEDia(Integer dentistaId, LocalDate data) {
        if (dentistaId == null || data == null) {
            return List.of();
        }

        Instant inicio = data.atStartOfDay(ZONA_HORARIA).toInstant();
        Instant fim = data.plusDays(1).atStartOfDay(ZONA_HORARIA).minusNanos(1).toInstant();
        return repository.findByDentistaEDia(dentistaId, inicio, fim);
    }

    public List<LocalTime> listarHorariosDisponiveis(Integer dentistaId, LocalDate data) {
        if (dentistaId == null || data == null || data.isBefore(LocalDate.now())) {
            return List.of();
        }

        Instant inicioDia = data.atStartOfDay(ZONA_HORARIA).toInstant();
        Instant fimDia = data.plusDays(1).atStartOfDay(ZONA_HORARIA).minusNanos(1).toInstant();
        List<Consulta> consultasOcupadas = repository.findOcupadasPorDentistaEntre(
                dentistaId,
                inicioDia,
                fimDia,
                EstadoConsulta.CANCELADA
        );

        return HORARIOS_BASE.stream()
                .filter(hora -> data.isAfter(LocalDate.now()) || hora.isAfter(LocalTime.now()))
                .filter(hora -> {
                    Instant inicio = data.atTime(hora).atZone(ZONA_HORARIA).toInstant();
                    Instant fim = inicio.plus(Duration.ofMinutes(DURACAO_PADRAO_MINUTOS));
                    return consultasOcupadas.stream().noneMatch(existente -> existeConflito(inicio, fim, existente));
                })
                .toList();
    }

    public boolean horarioDisponivel(Integer dentistaId, Instant inicio, Integer duracaoMinutos, Integer consultaIgnoradaId) {
        if (dentistaId == null || inicio == null || inicio.isBefore(Instant.now())) {
            return false;
        }

        int duracao = duracaoMinutos != null && duracaoMinutos > 0 ? duracaoMinutos : DURACAO_PADRAO_MINUTOS;
        LocalDate data = LocalDateTime.ofInstant(inicio, ZONA_HORARIA).toLocalDate();
        Instant inicioDia = data.atStartOfDay(ZONA_HORARIA).toInstant();
        Instant fimDia = data.plusDays(1).atStartOfDay(ZONA_HORARIA).minusNanos(1).toInstant();
        Instant fim = inicio.plus(Duration.ofMinutes(duracao));

        return repository.findOcupadasPorDentistaEntre(dentistaId, inicioDia, fimDia, EstadoConsulta.CANCELADA).stream()
                .filter(existente -> consultaIgnoradaId == null || !consultaIgnoradaId.equals(existente.getId()))
                .noneMatch(existente -> existeConflito(inicio, fim, existente));
    }

    public Consulta buscarPorId(Integer id) {
        return repository.findByIdEager(id)
                .orElseThrow(() -> new RuntimeException("Consulta nao encontrada"));
    }

    public Consulta atualizar(Consulta consulta) {
        buscarPorId(consulta.getId());
        return repository.save(consulta);
    }

    @Transactional
    public Consulta cancelar(Integer id, String motivoCancelamento) {
        if (motivoCancelamento == null || motivoCancelamento.isBlank()) {
            throw new IllegalArgumentException("O motivo de cancelamento e obrigatorio.");
        }

        Consulta consulta = buscarPorId(id);
        validarTransicao(consulta.getStatus(), EstadoConsulta.CANCELADA);
        validarAntecedenciaCancelamento(consulta);

        consulta.setStatus(EstadoConsulta.CANCELADA);
        consulta.setDataCancelamento(LocalDate.now());
        consulta.setMotivoCancelamento(motivoCancelamento.trim());
        return repository.save(consulta);
    }

    @Transactional
    public Consulta reagendar(Integer id, Instant novaDataHoraInicio) {
        Consulta consulta = buscarPorId(id);
        validarTransicao(consulta.getStatus(), EstadoConsulta.AGENDADA);

        if (novaDataHoraInicio == null) {
            throw new RuntimeException("A nova data da consulta e obrigatoria.");
        }
        if (novaDataHoraInicio.isBefore(Instant.now())) {
            throw new RuntimeException("Nao e possivel reagendar consulta para o passado.");
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
        Consulta consulta = atualizarStatus(id, EstadoConsulta.CONCLUIDA);        try {
            if (atendimentoService.buscarPorConsulta(consulta) == null) {
                model.Atendimento at = new model.Atendimento();
                at.setIdConsulta(consulta);
                at.setRetorno(false);
                at.setDiagnostico("Atendimento criado automaticamente ao concluir a consulta.");
                model.Atendimento salvo = atendimentoService.salvar(at);                try {
                    String nomeProc = resolverProcedimento(consulta);
                    if (nomeProc != null && !nomeProc.isBlank()) {
                        var procs = procedimentoRepository.findByNomeContainingIgnoreCase(nomeProc);
                        if (procs != null && !procs.isEmpty()) {
                            model.Procedimento proc = procs.get(0);
                            model.AtendimentoProcedimento ap = new model.AtendimentoProcedimento();
                            ap.setIdAtendimento(salvo);
                            ap.setIdProcedimento(proc);
                            ap.setQuantidade(1);
                            ap.setDesconto(java.math.BigDecimal.ZERO);
                            atendimentoProcedimentoRepository.save(ap);
                        }
                    }
                } catch (RuntimeException ignoredInner) {                }
            }
        } catch (RuntimeException ignored) {        }

        return consulta;
    }

    @Transactional
    public Consulta faturarConsulta(Integer id) {
        return atualizarStatus(id, EstadoConsulta.FATURADA);
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
                resolverProcedimento(consulta),
                consulta.getDataHoraInicio(),
                consulta.getStatus(),
                consulta.getIdPaciente() != null && consulta.getIdPaciente().getUtilizador() != null
                        ? consulta.getIdPaciente().getUtilizador().getNif()
                        : null,
                consulta.getIdPaciente() != null ? consulta.getIdPaciente().getId() : null
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

    private String resolverProcedimento(Consulta consulta) {
        if (consulta == null) {
            return null;
        }

        String procedimentoNasObservacoes = extrairLinhaPrefixada(consulta.getObservacoes(), "Procedimento:");
        if (procedimentoNasObservacoes != null && !procedimentoNasObservacoes.isBlank()) {
            return procedimentoNasObservacoes;
        }

        return consulta.getTipo();
    }

    private String extrairLinhaPrefixada(String texto, String prefixo) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        for (String linha : texto.split("\\R")) {
            if (linha != null && linha.startsWith(prefixo)) {
                return linha.substring(prefixo.length()).trim();
            }
        }
        return null;
    }

    private String normalizarTexto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim().toLowerCase();
    }

    private String normalizarPesquisa(String valor) {
        String texto = normalizarTexto(valor);
        if (texto == null) {
            return null;
        }
        return "%" + texto + "%";
    }

    private PeriodoInstants resolverPeriodo(String periodo) {
        if (periodo == null || periodo.isBlank()) {
            return new PeriodoInstants(null, null);
        }

        LocalDate hoje = LocalDate.now(ZONA_HORARIA);
        return switch (periodo.trim().toUpperCase()) {
            case "HOJE" -> new PeriodoInstants(
                    hoje.atStartOfDay(ZONA_HORARIA).toInstant(),
                    hoje.plusDays(1).atStartOfDay(ZONA_HORARIA).minusNanos(1).toInstant()
            );
            case "FUTURAS" -> new PeriodoInstants(Instant.now(), null);
            case "PASSADAS" -> new PeriodoInstants(null, Instant.now());
            default -> new PeriodoInstants(null, null);
        };
    }

    private record PeriodoInstants(Instant inicio, Instant fim) {
    }

    private void validarTransicao(EstadoConsulta statusAtual, EstadoConsulta novoStatus) {
        if (statusAtual == null || novoStatus == null) {
            throw new RuntimeException("Status da consulta invalido.");
        }

        if (statusAtual == novoStatus) {
            return;
        }

        boolean transicaoValida = switch (statusAtual) {
            case AGENDADA -> novoStatus == EstadoConsulta.AGENDADA
                    || novoStatus == EstadoConsulta.CONFIRMADA
                    || novoStatus == EstadoConsulta.CANCELADA;
            case CONFIRMADA -> novoStatus == EstadoConsulta.AGENDADA
                    || novoStatus == EstadoConsulta.EM_ESPERA
                    || novoStatus == EstadoConsulta.CANCELADA;
            case EM_ESPERA -> novoStatus == EstadoConsulta.EM_CONSULTA;
            case EM_CONSULTA -> novoStatus == EstadoConsulta.CONCLUIDA;
            case CONCLUIDA -> novoStatus == EstadoConsulta.FATURADA;
            case FATURADA, CANCELADA, FALTA, PENDENTE, EM_ATENDIMENTO -> false;
        };

        if (!transicaoValida) {
            throw new RuntimeException("Transicao de estado invalida: " + statusAtual + " -> " + novoStatus);
        }
    }

    private void validarAntecedenciaCancelamento(Consulta consulta) {
        if (consulta.getDataHoraInicio() == null) {
            return;
        }
        long horasAteConsulta = Duration.between(Instant.now(), consulta.getDataHoraInicio()).toHours();
        if (horasAteConsulta < HORAS_MINIMAS_CANCELAMENTO) {
            throw new IllegalStateException(
                "Nao e possivel cancelar com menos de " + HORAS_MINIMAS_CANCELAMENTO +
                " horas de antecedencia. Contacte a clinica diretamente."
            );
        }
    }

    private void validarConflitoHorario(Consulta consulta, Instant novaDataHoraInicio) {
        if (consulta.getIdDentista() == null || consulta.getIdDentista().getId() == null) {
            throw new RuntimeException("Consulta deve ter um dentista.");
        }

        try {
            boolean disponivel = horarioDisponivel(
                    consulta.getIdDentista().getId(),
                    novaDataHoraInicio,
                    consulta.getDuracao(),
                    consulta.getId()
            );

            if (!disponivel) {
                throw new RuntimeException("Este horario ja nao esta disponivel para o dentista selecionado.");
            }
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Nao foi possivel validar o novo horario da consulta.", ex);
        }
    }

    private boolean existeConflito(Instant novoInicio, Instant novoFim, Consulta existente) {
        if (existente.getDataHoraInicio() == null) {
            return false;
        }

        int duracaoExistente = existente.getDuracao() != null && existente.getDuracao() > 0
                ? existente.getDuracao()
                : DURACAO_PADRAO_MINUTOS;
        Instant existenteInicio = existente.getDataHoraInicio();
        Instant existenteFim = existenteInicio.plus(Duration.ofMinutes(duracaoExistente));
        return novoInicio.isBefore(existenteFim) && novoFim.isAfter(existenteInicio);
    }
}
