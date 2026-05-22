package controller;

import bll.ConsultaService;
import jakarta.servlet.http.HttpSession;
import model.dto.ConsultaAgendadaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Controller
public class ConsultasController {

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd MMM, yyyy");
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ConsultaService consultaService;

    public ConsultasController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/consultas")
    public String consultas(HttpSession session, Model model) {
        if (session.getAttribute("utilizadorId") == null) {
            return "redirect:/login";
        }

        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        String tipo = (String) session.getAttribute("utilizadorTipo");
        List<ConsultaAgendadaDTO> consultas = carregarConsultas(utilizadorId, tipo);
        List<ConsultaView> consultasView = consultas.stream()
                .map(this::toView)
                .toList();

        model.addAttribute("nomeUtilizador", session.getAttribute("utilizadorNome"));
        model.addAttribute("totalConsultas", consultasView.size());
        model.addAttribute("proximaVisita", calcularProximaVisita(consultas));
        model.addAttribute("periodoConsultas", calcularPeriodo(consultas));
        model.addAttribute("consultas", consultasView);

        return "consultas/index";
    }

    private List<ConsultaAgendadaDTO> carregarConsultas(Integer utilizadorId, String tipo) {
        List<ConsultaAgendadaDTO> consultas = consultaService.listarTodasAgendadas();

        if ("PACIENTE".equalsIgnoreCase(tipo) && utilizadorId != null) {
            return consultas.stream()
                    .filter(consulta -> utilizadorId.equals(consulta.getIdPaciente()))
                    .toList();
        }

        return consultas;
    }

    private ConsultaView toView(ConsultaAgendadaDTO consulta) {
        return new ConsultaView(
                valorOuPadrao(consulta.getNomeDentista(), "Dentista por definir"),
                valorOuPadrao(consulta.getProcedimento(), "Consulta"),
                formatarData(consulta.getDataHoraInicio()),
                formatarHora(consulta.getDataHoraInicio()),
                consulta.getStatus() != null ? consulta.getStatus().name() : "PENDENTE",
                resolverClasseBorda(consulta.getStatus() != null ? consulta.getStatus().name() : "PENDENTE"),
                resolverClasseBadge(consulta.getStatus() != null ? consulta.getStatus().name() : "PENDENTE")
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
            String dentista,
            String procedimento,
            String data,
            String hora,
            String status,
            String borderClass,
            String badgeClass
    ) {
    }
}
