package controller;

import bll.ConsultaService;
import bll.DentistaService;
import jakarta.servlet.http.HttpSession;
import model.Consulta;
import model.Dentista;
import model.dto.ConsultaAgendadaDTO;
import model.enums.EstadoConsulta;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Controller
public class ConsultasController {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd MMM, yyyy");
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ConsultaService consultaService;
    private final DentistaService dentistaService;

    public ConsultasController(ConsultaService consultaService, DentistaService dentistaService) {
        this.consultaService = consultaService;
        this.dentistaService = dentistaService;
    }

    @GetMapping("/consultas")
    public String consultas(
            HttpSession session,
            Model model,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer dentistaId,
            @RequestParam(required = false) String pesquisa,
            @RequestParam(required = false) String tipoConsulta
    ) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        String tipoUtilizador = (String) session.getAttribute("utilizadorTipo");
        boolean utilizadorPaciente = "PACIENTE".equalsIgnoreCase(tipoUtilizador);
        Integer pacienteId = utilizadorPaciente ? utilizadorId : null;
        String pesquisaPaciente = utilizadorPaciente ? null : pesquisa;
        EstadoConsulta statusSelecionado = parseEstado(estado);
        List<ConsultaAgendadaDTO> consultas = consultaService.filtrarConsultasAgendadas(
                statusSelecionado,
                dentistaId,
                pacienteId,
                dataInicio,
                dataFim,
                periodo,
                pesquisaPaciente,
                tipoConsulta
        );
        List<ConsultaView> consultasView = consultas.stream()
                .map(this::toView)
                .toList();

        model.addAttribute("nomeUtilizador", session.getAttribute("utilizadorNome"));
        model.addAttribute("totalConsultas", consultasView.size());
        model.addAttribute("proximaVisita", calcularProximaVisita(consultas));
        model.addAttribute("periodoConsultas", calcularPeriodo(consultas));
        model.addAttribute("consultas", consultasView);
        model.addAttribute("estadosConsulta", estadosConsultaFiltro());
        model.addAttribute("dentistas", dentistaService.listarTodos().stream().map(this::toDentistaOpcao).toList());
        model.addAttribute("tiposConsulta", consultaService.listarTiposConsulta());
        model.addAttribute("estadoSelecionado", statusSelecionado);
        model.addAttribute("periodoSelecionado", normalizarParametro(periodo));
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("dentistaSelecionadoId", dentistaId);
        model.addAttribute("mostrarFiltroPaciente", !utilizadorPaciente);
        model.addAttribute("pesquisa", normalizarParametro(pesquisaPaciente));
        model.addAttribute("tipoConsultaSelecionado", normalizarParametro(tipoConsulta));
        model.addAttribute("filtrosAplicados", filtrosAplicados(statusSelecionado, periodo, dataInicio, dataFim, dentistaId, pesquisaPaciente, tipoConsulta));

        return "consultas/index";
    }

    @GetMapping("/consultas/{id}/cancelar")
    public String mostrarCancelamento(
            @PathVariable Integer id,
            HttpSession session,
            Model model
    ) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("consulta", consultaService.buscarPorId(id));
            return "cancelar-consulta/index";
        } catch (RuntimeException ex) {
            return "redirect:/consultas";
        }
    }

    @PostMapping("/consultas/{id}/cancelar")
    public String confirmarCancelamento(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "") String motivo,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        // Autorização: paciente só pode cancelar as suas próprias consultas
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        String utilizadorTipo = (String) session.getAttribute("utilizadorTipo");

        try {
            if ("PACIENTE".equalsIgnoreCase(utilizadorTipo)) {
                Consulta consulta = consultaService.buscarPorId(id);
                boolean ePaciente = consulta.getIdPaciente() != null
                        && utilizadorId.equals(consulta.getIdPaciente().getId());
                if (!ePaciente) {
                    redirectAttributes.addFlashAttribute("mensagemErro",
                            "Não tem permissão para cancelar esta consulta.");
                    return "redirect:/consultas";
                }
            }

            consultaService.cancelar(id, motivo);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta cancelada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não foi possível cancelar a consulta. " + ex.getMessage());
        }

        return "redirect:/consultas";
    }

    @GetMapping("/consultas/{id}/reagendar")
    public String mostrarReagendar(
            @PathVariable Integer id,
            HttpSession session,
            Model model
    ) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        try {
            model.addAttribute("consulta", consultaService.buscarPorId(id));
            model.addAttribute("datas", proximasDatas());
            model.addAttribute("horarios", HORARIOS_DISPONIVEIS.stream()
                    .map(h -> DateTimeFormatter.ofPattern("HH:mm").format(h))
                    .toList());
            return "reagendar-consulta/index";
        } catch (RuntimeException ex) {
            return "redirect:/consultas";
        }
    }

    @PostMapping("/consultas/{id}/reagendar")
    public String confirmarReagendamento(
            @PathVariable Integer id,
            @RequestParam String data,
            @RequestParam String hora,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        try {
            LocalDate dataConsulta = LocalDate.parse(data);
            LocalTime horaConsulta = LocalTime.parse(hora);
            Instant novaDataHora = dataConsulta.atTime(horaConsulta)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            consultaService.reagendar(id, novaDataHora);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Consulta reagendada com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        }

        return "redirect:/consultas";
    }

    private static final List<LocalTime> HORARIOS_DISPONIVEIS = List.of(
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            LocalTime.of(11, 0),
            LocalTime.of(11, 30),
            LocalTime.of(14, 0),
            LocalTime.of(14, 30),
            LocalTime.of(15, 0),
            LocalTime.of(15, 30)
    );

    private List<DataOpcao> proximasDatas() {
        return LocalDate.now().plusDays(1)
                .datesUntil(LocalDate.now().plusDays(11))
                .map(this::toDataOpcao)
                .toList();
    }

    private DataOpcao toDataOpcao(LocalDate data) {
        return new DataOpcao(
                data.toString(),
                nomeDia(data.getDayOfWeek()),
                String.valueOf(data.getDayOfMonth()),
                DateTimeFormatter.ofPattern("MMM").format(data)
        );
    }

    private String nomeDia(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Seg";
            case TUESDAY -> "Ter";
            case WEDNESDAY -> "Qua";
            case THURSDAY -> "Qui";
            case FRIDAY -> "Sex";
            case SATURDAY -> "Sáb";
            case SUNDAY -> "Dom";
        };
    }

    private ConsultaView toView(ConsultaAgendadaDTO consulta) {
        String statusNome = consulta.getStatus() != null ? consulta.getStatus().name() : "PENDENTE";
        boolean podeAlterar = consulta.getStatus() == EstadoConsulta.AGENDADA
                || consulta.getStatus() == EstadoConsulta.CONFIRMADA;
        return new ConsultaView(
                consulta.getIdConsulta(),
                valorOuPadrao(consulta.getNomePaciente(), "Paciente por definir"),
                valorOuPadrao(consulta.getNomeDentista(), "Dentista por definir"),
                valorOuPadrao(consulta.getProcedimento(), "Consulta"),
                formatarData(consulta.getDataHoraInicio()),
                formatarHora(consulta.getDataHoraInicio()),
                labelStatus(consulta.getStatus()),
                resolverClasseBorda(statusNome),
                resolverClasseBadge(statusNome),
                podeAlterar
        );
    }

    private String calcularProximaVisita(List<ConsultaAgendadaDTO> consultas) {
        Instant agora = Instant.now();
        return consultas.stream()
                .map(ConsultaAgendadaDTO::getDataHoraInicio)
                .filter(data -> data != null && !data.isBefore(agora))
                .min(Comparator.naturalOrder())
                .map(data -> {
                    long dias = Duration.between(agora, data).toDays();
                    if (dias <= 0) {
                        return "Hoje";
                    }
                    return "Em " + dias + (dias == 1 ? " dia" : " dias");
                })
                .orElse("Sem data");
    }

    private String calcularPeriodo(List<ConsultaAgendadaDTO> consultas) {
        List<Instant> datas = consultas.stream()
                .map(ConsultaAgendadaDTO::getDataHoraInicio)
                .filter(data -> data != null)
                .sorted()
                .toList();

        if (datas.isEmpty()) {
            return "Sem consultas";
        }

        String inicio = DateTimeFormatter.ofPattern("MMM yyyy")
                .format(datas.get(0).atZone(ZoneId.systemDefault()));
        String fim = DateTimeFormatter.ofPattern("MMM yyyy")
                .format(datas.get(datas.size() - 1).atZone(ZoneId.systemDefault()));

        return inicio.equals(fim) ? inicio : inicio + " - " + fim;
    }

    private String formatarData(Instant data) {
        if (data == null) {
            return "Sem data";
        }
        return DATA_FORMATTER.format(data.atZone(ZoneId.systemDefault()));
    }

    private String formatarHora(Instant data) {
        if (data == null) {
            return "--:--";
        }
        return HORA_FORMATTER.format(data.atZone(ZoneId.systemDefault()));
    }

    private String valorOuPadrao(String valor, String padrao) {
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private EstadoConsulta parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }

        try {
            return EstadoConsulta.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String normalizarParametro(String valor) {
        return valor == null ? null : valor.trim();
    }

    private boolean filtrosAplicados(
            EstadoConsulta status,
            String periodo,
            LocalDate dataInicio,
            LocalDate dataFim,
            Integer dentistaId,
            String pesquisa,
            String tipoConsulta
    ) {
        return status != null
                || (periodo != null && !periodo.isBlank())
                || dataInicio != null
                || dataFim != null
                || dentistaId != null
                || (pesquisa != null && !pesquisa.isBlank())
                || (tipoConsulta != null && !tipoConsulta.isBlank());
    }

    private List<EstadoOpcao> estadosConsultaFiltro() {
        return List.of(
                new EstadoOpcao(EstadoConsulta.AGENDADA, "Agendada"),
                new EstadoOpcao(EstadoConsulta.CONFIRMADA, "Confirmada"),
                new EstadoOpcao(EstadoConsulta.CONCLUIDA, "Realizada"),
                new EstadoOpcao(EstadoConsulta.CANCELADA, "Cancelada"),
                new EstadoOpcao(EstadoConsulta.FATURADA, "Faturada")
        );
    }

    private DentistaOpcao toDentistaOpcao(Dentista dentista) {
        String nome = "Dentista por definir";
        if (dentista != null && dentista.getUtilizador() != null) {
            nome = valorOuPadrao(
                    (valorOuPadrao(dentista.getUtilizador().getPrimeiroNome(), "") + " "
                            + valorOuPadrao(dentista.getUtilizador().getUltimoNome(), "")).trim(),
                    nome
            );
        }
        return new DentistaOpcao(dentista != null ? dentista.getId() : null, nome);
    }

    private String labelStatus(EstadoConsulta status) {
        if (status == EstadoConsulta.CONCLUIDA) {
            return "REALIZADA";
        }
        return status != null ? status.name() : "PENDENTE";
    }

    private String resolverClasseBorda(String status) {
        return switch (status) {
            case "CONFIRMADA", "AGENDADA" -> "border-primary";
            case "PENDENTE", "EM_ESPERA" -> "border-tertiary";
            case "CANCELADA", "FALTA" -> "border-error";
            default -> "border-secondary";
        };
    }

    private String resolverClasseBadge(String status) {
        return switch (status) {
            case "CONFIRMADA", "AGENDADA" -> "bg-primary-container text-on-primary-container";
            case "PENDENTE", "EM_ESPERA" -> "bg-tertiary-container text-on-tertiary-container";
            case "CANCELADA", "FALTA" -> "bg-error-container text-on-error-container";
            default -> "bg-secondary-container text-on-secondary-container";
        };
    }

    public record ConsultaView(
            Integer id,
            String paciente,
            String dentista,
            String procedimento,
            String data,
            String hora,
            String status,
            String borderClass,
            String badgeClass,
            boolean podeAlterar
    ) {
    }

    public record DataOpcao(String valor, String diaSemana, String diaMes, String mes) {
    }

    public record EstadoOpcao(EstadoConsulta valor, String label) {
    }

    public record DentistaOpcao(Integer id, String nome) {
    }
}
