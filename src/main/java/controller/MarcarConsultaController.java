package controller;

import bll.ConsultaService;
import bll.DentistaService;
import jakarta.servlet.http.HttpSession;
import model.Consulta;
import model.Dentista;
import model.Paciente;
import model.enums.EstadoConsulta;
import org.springframework.stereotype.Controller;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MarcarConsultaController {

    private static final List<LocalTime> HORARIOS = List.of(
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

    private final DentistaService dentistaService;
    private final ConsultaService consultaService;

    public MarcarConsultaController(DentistaService dentistaService, ConsultaService consultaService) {
        this.dentistaService = dentistaService;
        this.consultaService = consultaService;
    }

    @GetMapping("/marcar-consulta")
    public String marcarConsulta(HttpSession session, Model model) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        carregarModelo(model, null, null, null, "Consulta Geral");
        if (!"PACIENTE".equalsIgnoreCase((String) session.getAttribute("utilizadorTipo"))) {
            model.addAttribute("erroMarcacao", "Para marcar uma consulta por esta pagina, entre com uma conta de paciente.");
        }
        return "marcar-consulta/index";
    }

    @PostMapping("/marcar-consulta")
    public String confirmarMarcacao(
            @RequestParam Integer dentistaId,
            @RequestParam String data,
            @RequestParam String hora,
            @RequestParam(defaultValue = "Consulta Geral") String tipo,
            HttpSession session,
            Model model
    ) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        String utilizadorTipo = (String) session.getAttribute("utilizadorTipo");

        if (utilizadorId == null) {
            return "redirect:/login";
        }

        if (!"PACIENTE".equalsIgnoreCase(utilizadorTipo)) {
            carregarModelo(model, dentistaId, data, hora, tipo);
            model.addAttribute("erroMarcacao", "Apenas contas de paciente podem marcar consulta diretamente.");
            return "marcar-consulta/index";
        }

        try {
            Consulta consulta = new Consulta();

            Paciente paciente = new Paciente();
            paciente.setId(utilizadorId);
            consulta.setIdPaciente(paciente);

            Dentista dentista = new Dentista();
            dentista.setId(dentistaId);
            consulta.setIdDentista(dentista);

            LocalDate dataConsulta = LocalDate.parse(data);
            LocalTime horaConsulta = LocalTime.parse(hora);
            Instant inicio = dataConsulta.atTime(horaConsulta)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();

            consulta.setDataHoraInicio(inicio);
            consulta.setDuracao(45);
            consulta.setTipo(tipo);
            consulta.setStatus(EstadoConsulta.AGENDADA);
            consulta.setDataMarcacao(LocalDate.now());

            consultaService.agendarConsulta(consulta);
            return "redirect:/consultas?marcada=sucesso";
        } catch (RuntimeException ex) {
            carregarModelo(model, dentistaId, data, hora, tipo);
            model.addAttribute("erroMarcacao", ex.getMessage());
            return "marcar-consulta/index";
        }
    }

    @GetMapping("/consultas/horarios-disponiveis")
    @ResponseBody
    public List<String> horariosDisponiveis(
            @RequestParam Integer dentistaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        return consultaService.listarHorariosDisponiveis(dentistaId, data).stream()
                .map(this::formatarHora)
                .toList();
    }

    private void carregarModelo(Model model, Integer dentistaSelecionado, String dataSelecionada,
                                String horaSelecionada, String tipoSelecionado) {
        List<DentistaOpcao> dentistas = dentistaService.listarAtivosOuCriarPadrao().stream()
                .map(this::toOpcao)
                .toList();

        model.addAttribute("dentistas", dentistas);
        model.addAttribute("datas", proximasDatas());
        Integer dentistaId = dentistaSelecionado != null
                ? dentistaSelecionado
                : dentistas.stream().findFirst().map(DentistaOpcao::id).orElse(null);
        String dataEscolhida = dataSelecionada != null ? dataSelecionada : LocalDate.now().plusDays(1).toString();
        List<String> horariosDisponiveis = listarHorariosDaData(dentistaId, dataEscolhida);

        model.addAttribute("horarios", horariosDisponiveis);
        model.addAttribute("dentistaSelecionado", dentistaId);
        model.addAttribute("dataSelecionada", dataEscolhida);
        model.addAttribute("horaSelecionada", horaSelecionada != null && horariosDisponiveis.contains(horaSelecionada)
                ? horaSelecionada
                : horariosDisponiveis.stream().findFirst().orElse(null));
        model.addAttribute("tipoSelecionado", tipoSelecionado != null ? tipoSelecionado : "Consulta Geral");
    }

    private List<String> listarHorariosDaData(Integer dentistaId, String dataSelecionada) {
        if (dentistaId == null || dataSelecionada == null || dataSelecionada.isBlank()) {
            return HORARIOS.stream().map(this::formatarHora).toList();
        }

        try {
            return consultaService.listarHorariosDisponiveis(dentistaId, LocalDate.parse(dataSelecionada)).stream()
                    .map(this::formatarHora)
                    .toList();
        } catch (RuntimeException ex) {
            return HORARIOS.stream().map(this::formatarHora).toList();
        }
    }

    private DentistaOpcao toOpcao(Dentista dentista) {
        String primeiroNome = dentista.getUtilizador() != null && dentista.getUtilizador().getPrimeiroNome() != null
                ? dentista.getUtilizador().getPrimeiroNome().trim()
                : "";
        String ultimoNome = dentista.getUtilizador() != null && dentista.getUtilizador().getUltimoNome() != null
                ? dentista.getUtilizador().getUltimoNome().trim()
                : "";
        String nome = (primeiroNome + " " + ultimoNome).trim();
        return new DentistaOpcao(dentista.getId(), nome.isBlank() ? "Dentista" : "Dr(a). " + nome, "Clinica Geral");
    }

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

    private String nomeDia(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Seg";
            case TUESDAY -> "Ter";
            case WEDNESDAY -> "Qua";
            case THURSDAY -> "Qui";
            case FRIDAY -> "Sex";
            case SATURDAY -> "Sab";
            case SUNDAY -> "Dom";
        };
    }

    private String formatarHora(LocalTime hora) {
        return DateTimeFormatter.ofPattern("HH:mm").format(hora);
    }

    public record DentistaOpcao(Integer id, String nome, String especialidade) {
    }

    public record DataOpcao(String valor, String diaSemana, String diaMes, String mes) {
    }
}
