package app;

import bll.*;
import model.*;
import model.enums.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.time.*;

@EntityScan(basePackages = "model")
@SpringBootApplication
@ComponentScan(basePackages = {"app", "bll", "dal", "model"})
@EnableJpaRepositories(basePackages = "dal")
public class MainApplication implements CommandLineRunner {

    private final UtilizadorService        utilizadorService;
    private final PacienteService          pacienteService;
    private final DentistaService          dentistaService;
    private final AssistenteService        assistenteService;
    private final RecepcionistaService     recepcionistaService;
    private final SeguroService            seguroService;
    private final PacientexSeguroService   pacientexSeguroService;
    private final ContatoEmergenciaService contatoEmergenciaService;
    private final ProntuarioService        prontuarioService;
    private final ConsultaService          consultaService;
    private final AtendimentoService       atendimentoService;
    private final FaturaService            faturaService;
    private final PagamentoService         pagamentoService;

    public MainApplication(
            UtilizadorService        utilizadorService,
            PacienteService          pacienteService,
            DentistaService          dentistaService,
            AssistenteService        assistenteService,
            RecepcionistaService     recepcionistaService,
            SeguroService            seguroService,
            PacientexSeguroService   pacientexSeguroService,
            ContatoEmergenciaService contatoEmergenciaService,
            ProntuarioService        prontuarioService,
            ConsultaService          consultaService,
            AtendimentoService       atendimentoService,
            FaturaService            faturaService,
            PagamentoService         pagamentoService) {

        this.utilizadorService        = utilizadorService;
        this.pacienteService          = pacienteService;
        this.dentistaService          = dentistaService;
        this.assistenteService        = assistenteService;
        this.recepcionistaService     = recepcionistaService;
        this.seguroService            = seguroService;
        this.pacientexSeguroService   = pacientexSeguroService;
        this.contatoEmergenciaService = contatoEmergenciaService;
        this.prontuarioService        = prontuarioService;
        this.consultaService          = consultaService;
        this.atendimentoService       = atendimentoService;
        this.faturaService            = faturaService;
        this.pagamentoService         = pagamentoService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void secao(String titulo) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.printf ("║  %-36s║%n", titulo);
        System.out.println("╚══════════════════════════════════════╝");
    }

    private static void ok(String msg)   { System.out.println("  ✔ " + msg); }
    private static void erro(String msg) { System.out.println("  ❌ ERRO: " + msg); }

    private Utilizador criarUtilizador(String nome, String apelido, String tipo) {
        Utilizador u = new Utilizador();
        u.setPrimeiroNome(nome);
        u.setUltimoNome(apelido);
        u.setEmail(nome.toLowerCase() + "." + apelido.toLowerCase()
                + "." + System.nanoTime() + "@clinica.pt");
        u.setTipoUtilizador(tipo);
        return utilizadorService.salvar(u);
    }

    // ── Runner ────────────────────────────────────────────────────────────────

    @Override
    public void run(String... args) {

        Utilizador  uPaciente      = null;
        Utilizador  uDentista      = null;
        Utilizador  uAssistente    = null;
        Utilizador  uRecepcionista = null;
        Paciente    paciente       = null;
        Dentista    dentista       = null;
        Seguro      seguro         = null;
        Consulta    consulta       = null;
        Atendimento atendimento    = null;
        Fatura      fatura         = null;

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 1 – UTILIZADORES
        // ══════════════════════════════════════════════════════════════════════
        secao("1. UTILIZADORES");

        try {
            uPaciente      = criarUtilizador("Ana",    "Costa",    "PACIENTE");
            uDentista      = criarUtilizador("Carlos", "Ferreira", "DENTISTA");
            uAssistente    = criarUtilizador("Sofia",  "Lopes",    "ASSISTENTE");
            uRecepcionista = criarUtilizador("Rui",    "Santos",   "RECEPCIONISTA");
            ok("4 utilizadores criados (IDs: "
                    + uPaciente.getId()      + ", "
                    + uDentista.getId()      + ", "
                    + uAssistente.getId()    + ", "
                    + uRecepcionista.getId() + ")");
        } catch (Exception e) { erro(e.getMessage()); }

        // Email duplicado deve ser rejeitado
        try {
            Utilizador dup = new Utilizador();
            dup.setPrimeiroNome("Dup");
            dup.setUltimoNome("Teste");
            dup.setEmail(uPaciente != null ? uPaciente.getEmail() : "dup@clinica.pt");
            dup.setTipoUtilizador("PACIENTE");
            utilizadorService.salvar(dup);
            erro("Deveria ter rejeitado email duplicado!");
        } catch (Exception e) {
            ok("Email duplicado rejeitado: " + e.getMessage());
        }

        // Nome obrigatório
        try {
            Utilizador sem = new Utilizador();
            sem.setEmail("semNome@clinica.pt");
            utilizadorService.salvar(sem);
            erro("Deveria ter rejeitado utilizador sem nome!");
        } catch (Exception e) {
            ok("Nome obrigatório validado: " + e.getMessage());
        }

        try {
            ok("Total de utilizadores na BD: " + utilizadorService.listarTodos().size());
        } catch (Exception e) { erro(e.getMessage()); }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 2 – PACIENTE
        // ══════════════════════════════════════════════════════════════════════
        secao("2. PACIENTE");

        try {
            if (uPaciente == null) throw new Exception("Utilizador paciente não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uPaciente.getId());
            Paciente p = new Paciente();
            p.setUtilizador(managed);
            p.setStatus("ATIVO");
            p.setDataRegisto(LocalDate.now());
            paciente = pacienteService.salvar(p);
            ok("Paciente criado com ID: " + paciente.getId());
        } catch (Exception e) { erro(e.getMessage()); }

        // Data de registo futura deve ser rejeitada
        try {
            if (uPaciente == null) throw new Exception("Utilizador paciente não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uPaciente.getId());
            Paciente pFut = new Paciente();
            pFut.setUtilizador(managed);
            pFut.setStatus("ATIVO");
            pFut.setDataRegisto(LocalDate.now().plusDays(1));
            pacienteService.salvar(pFut);
            erro("Deveria ter rejeitado data futura!");
        } catch (Exception e) {
            ok("Data de registo futura rejeitada: " + e.getMessage());
        }

        // Status obrigatório
        try {
            if (uPaciente == null) throw new Exception("Utilizador paciente não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uPaciente.getId());
            Paciente pSem = new Paciente();
            pSem.setUtilizador(managed);
            pacienteService.salvar(pSem);
            erro("Deveria ter rejeitado status vazio!");
        } catch (Exception e) {
            ok("Status obrigatório validado: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 3 – DENTISTA
        // ══════════════════════════════════════════════════════════════════════
        secao("3. DENTISTA");

        try {
            if (uDentista == null) throw new Exception("Utilizador dentista não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uDentista.getId());
            Dentista d = new Dentista();
            d.setUtilizador(managed);
            d.setNumeroOmd("OMD12345");
            d.setDataAdmissao(LocalDate.now().minusYears(2));
            d.setHorarioEntrada(LocalTime.of(9, 0));
            d.setHorarioSaida(LocalTime.of(18, 0));
            d.setAtivo(true);
            dentista = dentistaService.salvar(d);
            ok("Dentista criado com ID: " + dentista.getId());
        } catch (Exception e) { erro(e.getMessage()); }

        // Horário saída antes da entrada
        try {
            if (uDentista == null) throw new Exception("Utilizador dentista não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uDentista.getId());
            Dentista dInv = new Dentista();
            dInv.setUtilizador(managed);
            dInv.setNumeroOmd("OMD99999");
            dInv.setHorarioEntrada(LocalTime.of(18, 0));
            dInv.setHorarioSaida(LocalTime.of(9, 0));
            dentistaService.salvar(dInv);
            erro("Deveria ter rejeitado horário inválido!");
        } catch (Exception e) {
            ok("Horário inválido rejeitado: " + e.getMessage());
        }

        // OMD obrigatório
        try {
            if (uDentista == null) throw new Exception("Utilizador dentista não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uDentista.getId());
            Dentista dSem = new Dentista();
            dSem.setUtilizador(managed);
            dentistaService.salvar(dSem);
            erro("Deveria ter rejeitado dentista sem OMD!");
        } catch (Exception e) {
            ok("OMD obrigatório validado: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 4 – ASSISTENTE & RECEPCIONISTA
        // ══════════════════════════════════════════════════════════════════════
        secao("4. ASSISTENTE & RECEPCIONISTA");

        try {
            if (uAssistente == null) throw new Exception("Utilizador assistente não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uAssistente.getId());
            Assistente a = new Assistente();
            a.setUtilizador(managed);
            a.setNivelFormacao(NivelFormacao.SENIOR);
            a.setDataAdmissao(LocalDate.now().minusMonths(6));
            a.setAtivo(true);
            assistenteService.salvar(a);
            ok("Assistente criado");
        } catch (Exception e) { erro(e.getMessage()); }

        try {
            if (uRecepcionista == null) throw new Exception("Utilizador recepcionista não disponível");
            Utilizador managed = utilizadorService.buscarPorId(uRecepcionista.getId());
            Recepcionista r = new Recepcionista();
            r.setUtilizador(managed);
            r.setDataAdmissao(LocalDate.now().minusMonths(3));
            r.setTurno(Turno.MANHA);
            recepcionistaService.salvar(r);
            ok("Recepcionista criado");
        } catch (Exception e) { erro(e.getMessage()); }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 5 – SEGURO & PACIENTE×SEGURO
        // ══════════════════════════════════════════════════════════════════════
        secao("5. SEGURO & PACIENTE×SEGURO");

        try {
            Seguro s = new Seguro();
            s.setNomeSeguro("Medicare Plus");
            s.setCodigoPlano("MED-2025");
            s.setTipoPlano("COMPLETO");
            s.setValidoAte(LocalDate.now().plusYears(1));
            seguro = seguroService.salvar(s);
            ok("Seguro criado com ID: " + seguro.getId());
        } catch (Exception e) { erro(e.getMessage()); }

        // Associar paciente ao seguro
        try {
            if (paciente == null || seguro == null)
                throw new Exception("Paciente ou seguro não disponível");

            PacientexSeguroId psId = new PacientexSeguroId();
            psId.setIdUtilizador(paciente.getId());
            psId.setIdSeguro(seguro.getId());

            PacientexSeguro ps = new PacientexSeguro();
            ps.setId(psId);
            ps.setIdUtilizador(pacienteService.buscarPorId(paciente.getId()));
            ps.setIdSeguro(seguroService.buscarPorId(seguro.getId()));
            ps.setNumeroApolice("APOLICE-001");
            ps.setDataInicioCobertura(LocalDate.now());
            ps.setDataFimCobertura(LocalDate.now().plusYears(1));
            pacientexSeguroService.salvar(ps);
            ok("Paciente associado ao seguro");
        } catch (Exception e) { erro(e.getMessage()); }

        // Datas de cobertura invertidas
        try {
            if (paciente == null || seguro == null)
                throw new Exception("Paciente ou seguro não disponível");

            PacientexSeguroId psId2 = new PacientexSeguroId();
            psId2.setIdUtilizador(paciente.getId());
            psId2.setIdSeguro(seguro.getId());

            PacientexSeguro psInv = new PacientexSeguro();
            psInv.setId(psId2);
            psInv.setIdUtilizador(pacienteService.buscarPorId(paciente.getId()));
            psInv.setIdSeguro(seguroService.buscarPorId(seguro.getId()));
            psInv.setDataInicioCobertura(LocalDate.now().plusYears(1));
            psInv.setDataFimCobertura(LocalDate.now());
            pacientexSeguroService.salvar(psInv);
            erro("Deveria ter rejeitado datas invertidas!");
        } catch (Exception e) {
            ok("Datas de cobertura invertidas rejeitadas: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 6 – CONTATO DE EMERGÊNCIA
        // ══════════════════════════════════════════════════════════════════════
        secao("6. CONTATO DE EMERGÊNCIA");

        try {
            if (paciente == null) throw new Exception("Paciente não disponível");
            ContatoEmergencia ce = new ContatoEmergencia();
            ce.setPaciente(pacienteService.buscarPorId(paciente.getId()));
            ce.setPrimeiroNome("Maria");
            ce.setUltimoNome("Costa");
            contatoEmergenciaService.salvar(ce);
            ok("Contato de emergência criado");
        } catch (Exception e) { erro(e.getMessage()); }

        // Nome obrigatório
        try {
            if (paciente == null) throw new Exception("Paciente não disponível");
            ContatoEmergencia ceSem = new ContatoEmergencia();
            ceSem.setPaciente(pacienteService.buscarPorId(paciente.getId()));
            contatoEmergenciaService.salvar(ceSem);
            erro("Deveria ter rejeitado contato sem nome!");
        } catch (Exception e) {
            ok("Nome de contato obrigatório validado: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 7 – PRONTUÁRIO
        // ══════════════════════════════════════════════════════════════════════
        secao("7. PRONTUÁRIO");

        try {
            if (paciente == null) throw new Exception("Paciente não disponível");
            Prontuario pr = new Prontuario();
            pr.setPaciente(pacienteService.buscarPorId(paciente.getId()));
            pr.setGrupoSanguineo("A+");
            pr.setObservacoes("Paciente sem alergias conhecidas.");
            prontuarioService.criarProntuario(pr);
            ok("Prontuário criado");
        } catch (Exception e) { erro(e.getMessage()); }

        // Prontuário duplicado
        try {
            if (paciente == null) throw new Exception("Paciente não disponível");
            Prontuario prDup = new Prontuario();
            prDup.setPaciente(pacienteService.buscarPorId(paciente.getId()));
            prDup.setGrupoSanguineo("B+");
            prontuarioService.criarProntuario(prDup);
            erro("Deveria ter rejeitado prontuário duplicado!");
        } catch (Exception e) {
            ok("Prontuário duplicado rejeitado: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 8 – CONSULTA
        // ══════════════════════════════════════════════════════════════════════
        secao("8. CONSULTA");

        try {
            if (paciente == null || dentista == null)
                throw new Exception("Paciente ou dentista não disponível");
            Consulta c = new Consulta();
            c.setIdPaciente(pacienteService.buscarPorId(paciente.getId()));
            c.setIdDentista(dentistaService.buscarPorId(dentista.getId()));
            c.setDataHoraInicio(Instant.now().plusSeconds(86400));
            c.setDuracao(30);
            c.setTipo("CONSULTA_GERAL");
            c.setStatus(EstadoConsulta.AGENDADA);
            c.setDataMarcacao(LocalDate.now());
            consulta = consultaService.agendarConsulta(c);
            ok("Consulta agendada com ID: " + consulta.getId());
        } catch (Exception e) { erro(e.getMessage()); }

        // Consulta no passado
        try {
            if (paciente == null || dentista == null)
                throw new Exception("Paciente ou dentista não disponível");
            Consulta cPass = new Consulta();
            cPass.setIdPaciente(pacienteService.buscarPorId(paciente.getId()));
            cPass.setIdDentista(dentistaService.buscarPorId(dentista.getId()));
            cPass.setDataHoraInicio(Instant.now().minusSeconds(3600));
            cPass.setStatus(EstadoConsulta.AGENDADA);
            consultaService.agendarConsulta(cPass);
            erro("Deveria ter rejeitado consulta no passado!");
        } catch (Exception e) {
            ok("Consulta no passado rejeitada: " + e.getMessage());
        }

        // Consulta sem paciente
        try {
            Consulta cSem = new Consulta();
            cSem.setDataHoraInicio(Instant.now().plusSeconds(86400));
            cSem.setStatus(EstadoConsulta.AGENDADA);
            consultaService.agendarConsulta(cSem);
            erro("Deveria ter rejeitado consulta sem paciente!");
        } catch (Exception e) {
            ok("Consulta sem paciente rejeitada: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 9 – ATENDIMENTO
        // ══════════════════════════════════════════════════════════════════════
        secao("9. ATENDIMENTO");

        try {
            if (consulta == null) throw new Exception("Consulta não disponível");
            Atendimento at = new Atendimento();
            at.setIdConsulta(consultaService.buscarPorId(consulta.getId()));
            at.setDiagnostico("Cárie grau 2 no dente 36.");
            at.setRetorno(true);
            at.setPeriodoRetorno(30);
            at.setObservacoes("Aplicar selante após tratamento.");
            atendimento = atendimentoService.salvar(at);
            ok("Atendimento criado com ID: " + atendimento.getId());
        } catch (Exception e) { erro(e.getMessage()); }

        // Retorno sem período
        try {
            if (consulta == null) throw new Exception("Consulta não disponível");
            Atendimento atInv = new Atendimento();
            atInv.setIdConsulta(consultaService.buscarPorId(consulta.getId()));
            atInv.setRetorno(true);
            atendimentoService.salvar(atInv);
            erro("Deveria ter rejeitado retorno sem período!");
        } catch (Exception e) {
            ok("Retorno sem período rejeitado: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 10 – FATURA & PAGAMENTO
        // ══════════════════════════════════════════════════════════════════════
        secao("10. FATURA & PAGAMENTO");

        try {
            if (atendimento == null) throw new Exception("Atendimento não disponível");
            Fatura f = new Fatura();
            f.setIdAtendimento(atendimentoService.buscarPorId(atendimento.getId()));
            f.setValorFinal(new BigDecimal("150.00"));
            f.setEstado(EstadoFatura.PENDENTE);
            fatura = faturaService.emitirFatura(f);
            ok("Fatura emitida com ID: " + fatura.getId()
                    + " | Valor: " + fatura.getValorFinal() + "€"
                    + " | Data: " + fatura.getDataEmissao());
        } catch (Exception e) { erro(e.getMessage()); }

        // Valor zero
        try {
            if (atendimento == null) throw new Exception("Atendimento não disponível");
            Fatura fInv = new Fatura();
            fInv.setIdAtendimento(atendimentoService.buscarPorId(atendimento.getId()));
            fInv.setValorFinal(BigDecimal.ZERO);
            faturaService.emitirFatura(fInv);
            erro("Deveria ter rejeitado fatura com valor zero!");
        } catch (Exception e) {
            ok("Valor zero rejeitado: " + e.getMessage());
        }

        // Registar pagamento
        try {
            if (fatura == null || uPaciente == null)
                throw new Exception("Fatura ou utilizador não disponível");
            Pagamento pg = new Pagamento();
            pg.setIdFatura(faturaService.buscarPorId(fatura.getId()));
            pg.setIdUtilizador(utilizadorService.buscarPorId(uPaciente.getId()));
            pg.setValorPago(new BigDecimal("150.00"));
            pg.setMetodo(MetodoPagamento.MBWAY);
            pg.setDataPagamento(LocalDate.now());
            Pagamento pgSalvo = pagamentoService.registrarPagamento(pg);
            ok("Pagamento registado com ID: " + pgSalvo.getId()
                    + " | Método: " + pgSalvo.getMetodo());
        } catch (Exception e) { erro(e.getMessage()); }

        // Data de pagamento futura
        try {
            if (fatura == null) throw new Exception("Fatura não disponível");
            Pagamento pgFut = new Pagamento();
            pgFut.setIdFatura(faturaService.buscarPorId(fatura.getId()));
            pgFut.setValorPago(new BigDecimal("50.00"));
            pgFut.setDataPagamento(LocalDate.now().plusDays(5));
            pagamentoService.registrarPagamento(pgFut);
            erro("Deveria ter rejeitado data de pagamento futura!");
        } catch (Exception e) {
            ok("Data de pagamento futura rejeitada: " + e.getMessage());
        }

        // ══════════════════════════════════════════════════════════════════════
        // BLOCO 11 – LISTAGENS FINAIS
        // ══════════════════════════════════════════════════════════════════════
        secao("11. LISTAGENS FINAIS");

        try {
            System.out.println("  Utilizadores (" + utilizadorService.listarTodos().size() + "):");
            utilizadorService.listarTodos().forEach(u ->
                    System.out.println("    • [" + u.getTipoUtilizador() + "] "
                            + u.getPrimeiroNome() + " " + u.getUltimoNome()
                            + " — " + u.getEmail()));
        } catch (Exception e) { erro(e.getMessage()); }

        try {
            System.out.println("  Pacientes    : " + pacienteService.listarTodos().size());
            System.out.println("  Dentistas    : " + dentistaService.listarTodos().size());
            System.out.println("  Consultas    : " + consultaService.listarTodas().size());
            System.out.println("  Atendimentos : " + atendimentoService.listarTodos().size());
            System.out.println("  Faturas      : " + faturaService.listarTodos().size());
            System.out.println("  Pagamentos   : " + pagamentoService.listarTodos().size());
        } catch (Exception e) { erro(e.getMessage()); }

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║      TODOS OS TESTES CONCLUÍDOS      ║");
        System.out.println("╚══════════════════════════════════════╝\n");
    }
}