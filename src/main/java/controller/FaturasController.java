package controller;

import bll.FaturaService;
import jakarta.servlet.http.HttpSession;
import model.Atendimento;
import model.AtendimentoProcedimento;
import model.Consulta;
import model.Fatura;
import model.Procedimento;
import model.Utilizador;
import model.enums.EstadoFatura;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
public class FaturasController {

    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM, yyyy");

    private final FaturaService faturaService;

    @Value("${app.faturas-dir:C:/Users/jenni/intelijProjetos/clinica/uploads/faturas}")
    private String faturasDir;

    public FaturasController(FaturaService faturaService) {
        this.faturaService = faturaService;
    }

    @GetMapping("/faturas")
    public String faturas(HttpSession session, Model model) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) {
            return "redirect:/login";
        }

        String tipoUtilizador = (String) session.getAttribute("utilizadorTipo");
        List<Fatura> faturas = faturaService.listarTodos().stream()
                .filter(fatura -> deveMostrarFatura(fatura, tipoUtilizador, utilizadorId))
                .toList();
        List<FaturaView> faturasView = faturas.stream()
                .map(this::toView)
                .toList();

        BigDecimal saldoAberto = faturas.stream()
                .filter(fatura -> fatura.getEstado() != EstadoFatura.PAGA)
                .map(this::valorFinalSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("faturas", faturasView);
        model.addAttribute("saldoAberto", CURRENCY_FORMAT.format(saldoAberto));
        return "faturas/index";
    }

    @GetMapping("/faturas/{id}/download")
    public ResponseEntity<Resource> descarregarFatura(@PathVariable Integer id, HttpSession session) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Fatura fatura;
        try {
            fatura = faturaService.buscarPorId(id);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fatura nao encontrada.");
        }

        String tipoUtilizador = (String) session.getAttribute("utilizadorTipo");
        if (!deveMostrarFatura(fatura, tipoUtilizador, utilizadorId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (fatura.getEstado() != EstadoFatura.PAGA) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "O documento ainda nao esta disponivel.");
        }

        Path caminho = faturaService.resolverCaminhoPdf(fatura, faturasDir);
        if (caminho == null || !Files.isRegularFile(caminho) || !Files.isReadable(caminho)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "O documento ainda nao esta disponivel.");
        }

        Resource recurso = new FileSystemResource(caminho);
        String nomeFicheiro = caminho.getFileName().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeFicheiro + "\"")
                .body(recurso);
    }

    private boolean deveMostrarFatura(Fatura fatura, String tipoUtilizador, Integer utilizadorId) {
        if (!"PACIENTE".equalsIgnoreCase(tipoUtilizador)) {
            return true;
        }

        try {
            Consulta consulta = fatura.getIdAtendimento().getIdConsulta();
            return consulta != null
                    && consulta.getIdPaciente() != null
                    && utilizadorId.equals(consulta.getIdPaciente().getId());
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private FaturaView toView(Fatura fatura) {
        EstadoFatura estado = fatura.getEstado() != null ? fatura.getEstado() : EstadoFatura.PENDENTE;
        boolean pdfDisponivel = estado == EstadoFatura.PAGA && faturaService.temPdfDisponivel(fatura, faturasDir);
        return new FaturaView(
                fatura.getId(),
                tituloTratamento(fatura),
                nomeDentista(fatura),
                formatarData(fatura.getDataEmissao()),
                CURRENCY_FORMAT.format(valorFinalSeguro(fatura)),
                estado.name(),
                classeEstado(estado),
                pontoEstado(estado),
                pdfDisponivel
        );
    }

    private String tituloTratamento(Fatura fatura) {
        try {
            Atendimento atendimento = fatura.getIdAtendimento();
            if (atendimento != null && atendimento.getProcedimentos() != null && !atendimento.getProcedimentos().isEmpty()) {
                AtendimentoProcedimento item = atendimento.getProcedimentos().get(0);
                Procedimento procedimento = item.getProcedimento();
                if (procedimento != null && procedimento.getNome() != null && !procedimento.getNome().isBlank()) {
                    return procedimento.getNome();
                }
            }

            Consulta consulta = atendimento != null ? atendimento.getIdConsulta() : null;
            if (consulta != null && consulta.getTipo() != null && !consulta.getTipo().isBlank()) {
                return consulta.getTipo();
            }
        } catch (RuntimeException ignored) {
        }

        return "Fatura #" + fatura.getId();
    }

    private String nomeDentista(Fatura fatura) {
        try {
            Consulta consulta = fatura.getIdAtendimento().getIdConsulta();
            if (consulta == null || consulta.getIdDentista() == null) {
                return "Clinica Dentaria";
            }

            Utilizador utilizador = consulta.getIdDentista().getUtilizador();
            if (utilizador == null) {
                return "Clinica Dentaria";
            }

            String primeiroNome = utilizador.getPrimeiroNome() != null ? utilizador.getPrimeiroNome().trim() : "";
            String ultimoNome = utilizador.getUltimoNome() != null ? utilizador.getUltimoNome().trim() : "";
            String nome = (primeiroNome + " " + ultimoNome).trim();
            return nome.isBlank() ? "Clinica Dentaria" : "Dr(a). " + nome;
        } catch (RuntimeException ex) {
            return "Clinica Dentaria";
        }
    }

    private String formatarData(LocalDate data) {
        return data == null ? "Sem data" : DATE_FORMATTER.format(data);
    }

    private BigDecimal valorFinalSeguro(Fatura fatura) {
        BigDecimal valor = fatura.getValorFinal();
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private String classeEstado(EstadoFatura estado) {
        return switch (estado) {
            case PAGA -> "bg-primary-container text-on-primary-container";
            case PENDENTE -> "bg-tertiary-container text-on-tertiary-container";
            case CANCELADA, ANULADA -> "bg-error-container text-on-error-container";
        };
    }

    private String pontoEstado(EstadoFatura estado) {
        return switch (estado) {
            case PAGA -> "bg-primary";
            case PENDENTE -> "bg-tertiary";
            case CANCELADA, ANULADA -> "bg-error";
        };
    }

    public record FaturaView(
            Integer id,
            String tratamento,
            String dentista,
            String dataEmissao,
            String valor,
            String estado,
            String classeEstado,
            String pontoEstado,
            boolean pdfDisponivel
    ) {
    }
}
