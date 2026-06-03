package controller;

import bll.FaturaService;
import bll.FaturaService.FaturaAcessoNegadoException;
import jakarta.servlet.http.HttpSession;
import model.Fatura;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Endpoints da área do paciente para listagem e download de faturas.
 *
 * Segurança:
 *   - Todas as rotas verificam a sessão antes de agir.
 *   - O download verifica que a fatura pertence ao paciente logado,
 *     navegando a cadeia Fatura → Atendimento → Consulta → Paciente.
 *   - O caminho físico do PDF nunca é exposto no HTML a URL usa apenas o id.
 */
@Controller
@RequestMapping("/paciente/faturas")
public class FaturaDownloadController {

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("pt", "PT"));
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FaturaService faturaService;

    /** Pasta base dos PDFs: configurada em application.properties (app.faturas-dir). */
    @Value("${app.faturas-dir:C:/Users/jenni/intelijProjetos/clinica/uploads/faturas}")
    private String faturasDir;

    public FaturaDownloadController(FaturaService faturaService) {
        this.faturaService = faturaService;
    }

    // ── GET /paciente/faturas ─────────────────────────────────────────────────

    @GetMapping
    public String listarFaturas(HttpSession session, Model model) {
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) return "redirect:/login";

        List<Fatura> faturas = faturaService.listarPorPaciente(utilizadorId);

        List<FaturaView> views = faturas.stream()
                .map(f -> toView(f, utilizadorId))
                .toList();

        BigDecimal totalPendente = faturas.stream()
                .filter(f -> f.getEstado() == EstadoFatura.PENDENTE)
                .map(f -> f.getValorFinal() != null ? f.getValorFinal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("faturas", views);
        model.addAttribute("totalPendente", CURRENCY.format(totalPendente));
        model.addAttribute("nomeUtilizador", session.getAttribute("utilizadorNome"));
        return "paciente/faturas";
    }

    // ── GET /paciente/faturas/{id}/download ───────────────────────────────────

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> descarregarFatura(
            @PathVariable Integer id,
            HttpSession session
    ) {
        // ── Autenticação ──────────────────────────────────────────────────────
        Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
        if (utilizadorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ── 1. Buscar fatura e validar que pertence ao paciente logado ────────
        Fatura fatura;
        try {
            fatura = faturaService.buscarPorIdEPaciente(id, utilizadorId);
        } catch (FaturaAcessoNegadoException ex) {
            // 403 — não revelar se a fatura existe noutro paciente
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // ── 2. Verificar estado PAGA ──────────────────────────────────────────
        if (fatura.getEstado() != EstadoFatura.PAGA) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "O documento ainda não está disponível.");
        }

        // ── 3. Verificar caminhoPdf preenchido ────────────────────────────────
        String caminhoRaw = fatura.getCaminhoPdf();
        if (caminhoRaw == null || caminhoRaw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "O documento ainda não está disponível.");
        }

        // ── 4. Normalizar caminho (resolve barras mistas / e \ do Windows) ────
        // O service já contém toda a lógica de normalização + fallback.
        // Reutilizamos aqui para não duplicar código.
        Path caminho = faturaService.resolverCaminhoPdf(fatura, faturasDir);
        if (caminho == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "O documento ainda não está disponível.");
        }

        // ── Debug (remover em produção) ───────────────────────────────────────
        System.out.println("[FaturaDownload] faturaId=" + id);
        System.out.println("[FaturaDownload] caminhoPdf BD: " + caminhoRaw);
        System.out.println("[FaturaDownload] caminho absoluto: " + caminho);
        System.out.println("[FaturaDownload] existe? " + Files.exists(caminho));
        System.out.println("[FaturaDownload] legível? " + Files.isReadable(caminho));

        // ── 5. Verificar existência e legibilidade do ficheiro ─────────────────
        if (!Files.exists(caminho) || !Files.isReadable(caminho)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "O documento ainda não está disponível.");
        }

        // ── 6. Servir o PDF ───────────────────────────────────────────────────
        Resource recurso = new FileSystemResource(caminho);
        String nomeFicheiro = caminho.getFileName().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nomeFicheiro + "\"")
                .body(recurso);
    }

    // ── View model ────────────────────────────────────────────────────────────

    private FaturaView toView(Fatura fatura, Integer utilizadorId) {
        EstadoFatura estado = fatura.getEstado() != null
                ? fatura.getEstado() : EstadoFatura.PENDENTE;

        boolean temPdf   = faturaService.temPdfDisponivel(fatura, faturasDir);
        boolean podeBaixar = estado == EstadoFatura.PAGA && temPdf;

        return new FaturaView(
                fatura.getId(),
                descricaoFatura(fatura),
                formatarData(fatura.getDataEmissao()),
                CURRENCY.format(fatura.getValorFinal() != null
                        ? fatura.getValorFinal() : BigDecimal.ZERO),
                estado.name(),
                labelEstado(estado),
                corBadge(estado),
                podeBaixar,
                temPdf
        );
    }

    private String descricaoFatura(Fatura fatura) {
        try {
            var procedimentos = fatura.getIdAtendimento().getProcedimentos();
            if (procedimentos != null && !procedimentos.isEmpty()) {
                var proc = procedimentos.get(0).getProcedimento();
                if (proc != null && proc.getNome() != null && !proc.getNome().isBlank()) {
                    return proc.getNome();
                }
            }
            String tipo = fatura.getIdAtendimento().getIdConsulta().getTipo();
            if (tipo != null && !tipo.isBlank()) return tipo;
        } catch (RuntimeException ignored) { }
        return "Fatura nº " + fatura.getId();
    }

    private String formatarData(LocalDate data) {
        return data == null ? "—" : DATE_FMT.format(data);
    }

    private String labelEstado(EstadoFatura estado) {
        return switch (estado) {
            case PAGA      -> "Paga";
            case PENDENTE  -> "Pendente";
            case CANCELADA -> "Cancelada";
            case ANULADA   -> "Anulada";
        };
    }

    private String corBadge(EstadoFatura estado) {
        return switch (estado) {
            case PAGA      -> "bg-primary-container text-on-primary-container";
            case PENDENTE  -> "bg-tertiary-container text-on-tertiary-container";
            case CANCELADA, ANULADA -> "bg-error-container text-on-error-container";
        };
    }

    // ── Record de vista ───────────────────────────────────────────────────────

    public record FaturaView(
            Integer id,
            String descricao,
            String dataEmissao,
            String valor,
            String estadoCodigo,
            String estadoLabel,
            String corBadge,
            boolean podeBaixar,
            boolean temPdf
    ) { }
}
