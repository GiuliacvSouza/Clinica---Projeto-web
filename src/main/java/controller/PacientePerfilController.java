package controller;

import app.MainFX;
import app.SceneManager;
import app.SessionContext;
import bll.ConsultaService;
import bll.PacienteService;
import bll.PacientexSeguroService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Consulta;
import model.Paciente;
import model.PacientexSeguro;
import model.Utilizador;
import model.enums.EstadoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PacientePerfilController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");

    @FXML private Label nomeUtilizador;
    @FXML private Label lblPacienteNome;
    @FXML private Label lblPacienteMeta;
    @FXML private Label lblPacienteSeguro;
    @FXML private Label lblPacienteRegisto;
    @FXML private Label lblAvatarIniciais;
    @FXML private Label lblResumoConsultas;
    @FXML private Label lblResumoUltimaConsulta;
    @FXML private Label lblResumoStatus;
    @FXML private Label lblHistoricoVazio;
    @FXML private TextField txtPrimeiroNome;
    @FXML private TextField txtUltimoNome;
    @FXML private TextField txtNif;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelemovel;
    @FXML private TextField txtTelefone;
    @FXML private DatePicker dpNascimento;
    @FXML private TextField txtRua;
    @FXML private TextField txtNumeroPorta;
    @FXML private ComboBox<String> cbStatus;
    @FXML private Button btnEditarPerfil;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private VBox historicoContainer;

    @Autowired private PacienteService pacienteService;
    @Autowired private PacientexSeguroService pacientexSeguroService;
    @Autowired private ConsultaService consultaService;

    private Integer pacienteId;
    private Paciente pacienteAtual;
    private boolean modoEdicao;

    @FXML
    public void initialize() {
        Utilizador utilizadorLogado = SessionContext.getUtilizadorLogado();
        if (utilizadorLogado != null && nomeUtilizador != null) {
            nomeUtilizador.setText(formatarNome(utilizadorLogado));
        }

        cbStatus.getItems().setAll("ATIVO", "INATIVO");
        atualizarModoEdicao(false);
    }

    public void setPacienteId(Integer pacienteId) {
        this.pacienteId = pacienteId;
        carregarPaciente();
    }

    @FXML
    private void ativarEdicao() {
        if (pacienteAtual == null) {
            mostrarErro("Paciente nao encontrado.");
            return;
        }
        atualizarModoEdicao(true);
    }

    @FXML
    private void cancelarEdicao() {
        atualizarModoEdicao(false);
        preencherFormulario();
    }

    @FXML
    private void guardarEdicao() {
        if (pacienteAtual == null) {
            mostrarErro("Paciente nao encontrado.");
            return;
        }

        try {
            validarFormulario();

            Utilizador utilizador = pacienteAtual.getUtilizador();
            utilizador.setPrimeiroNome(txtPrimeiroNome.getText().trim());
            utilizador.setUltimoNome(txtUltimoNome.getText().trim());
            utilizador.setNif(txtNif.getText().trim());
            utilizador.setEmail(txtEmail.getText().trim());
            utilizador.setTelemovel(valorOuNull(txtTelemovel.getText()));
            utilizador.setTelefone(valorOuNull(txtTelefone.getText()));
            utilizador.setDataNascimento(dpNascimento.getValue());
            utilizador.setRua(valorOuNull(txtRua.getText()));
            utilizador.setNumeroPorta(valorOuNull(txtNumeroPorta.getText()));
            pacienteAtual.setStatus(cbStatus.getValue());

            pacienteAtual = pacienteService.salvar(pacienteAtual);
            atualizarModoEdicao(false);
            carregarPaciente();
            mostrarInformacao("Perfil do paciente atualizado com sucesso.");
        } catch (RuntimeException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void carregarPaciente() {
        if (pacienteId == null) {
            return;
        }

        try {
            pacienteAtual = pacienteService.buscarPorId(pacienteId);
            preencherCabecalho();
            preencherFormulario();
            preencherResumo();
            preencherHistorico();
        } catch (RuntimeException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void preencherCabecalho() {
        Utilizador utilizador = pacienteAtual.getUtilizador();
        String nome = valorOuPadrao(formatarNome(utilizador));

        lblPacienteNome.setText(nome);
        lblPacienteMeta.setText("NIF " + valorOuPadrao(utilizador.getNif()) + "  |  " + valorOuPadrao(primeiroValorPreenchido(
                utilizador.getTelemovel(),
                utilizador.getTelefone(),
                utilizador.getEmail()
        )));
        lblPacienteSeguro.setText(obterResumoSeguro());
        lblPacienteRegisto.setText("Registado em " + formatarData(pacienteAtual.getDataRegisto()));
        lblAvatarIniciais.setText(calcularIniciais(utilizador));
    }

    private void preencherFormulario() {
        Utilizador utilizador = pacienteAtual.getUtilizador();
        txtPrimeiroNome.setText(valorVazio(utilizador.getPrimeiroNome()));
        txtUltimoNome.setText(valorVazio(utilizador.getUltimoNome()));
        txtNif.setText(valorVazio(utilizador.getNif()));
        txtEmail.setText(valorVazio(utilizador.getEmail()));
        txtTelemovel.setText(valorVazio(utilizador.getTelemovel()));
        txtTelefone.setText(valorVazio(utilizador.getTelefone()));
        dpNascimento.setValue(utilizador.getDataNascimento());
        txtRua.setText(valorVazio(utilizador.getRua()));
        txtNumeroPorta.setText(valorVazio(utilizador.getNumeroPorta()));
        cbStatus.setValue(valorOuPadrao(pacienteAtual.getStatus()).equals("-") ? "ATIVO" : pacienteAtual.getStatus());
    }

    private void preencherResumo() {
        List<Consulta> consultas = consultaService.listarPorPaciente(pacienteAtual.getId());
        lblResumoConsultas.setText(String.valueOf(consultas.size()));
        lblResumoUltimaConsulta.setText(consultas.isEmpty()
                ? "Sem consultas"
                : formatarDataHora(consultas.stream()
                        .map(Consulta::getDataHoraInicio)
                        .filter(Objects::nonNull)
                        .max(Comparator.naturalOrder())
                        .orElse(null)));
        lblResumoStatus.setText(valorOuPadrao(pacienteAtual.getStatus()));
    }

    private void preencherHistorico() {
        historicoContainer.getChildren().clear();

        List<Consulta> consultas = consultaService.listarPorPaciente(pacienteAtual.getId());
        lblHistoricoVazio.setVisible(consultas.isEmpty());
        lblHistoricoVazio.setManaged(consultas.isEmpty());

        for (Consulta consulta : consultas) {
            historicoContainer.getChildren().add(criarCardConsulta(consulta));
        }
    }

    private VBox criarCardConsulta(Consulta consulta) {
        VBox card = new VBox(12);
        card.getStyleClass().add("patient-history-card");

        HBox header = new HBox(12);
        VBox titleBox = new VBox(4);
        Label titulo = new Label(valorOuPadrao(resolverProcedimento(consulta)));
        titulo.getStyleClass().add("patient-history-title");
        Label subtitulo = new Label(formatarDataHora(consulta.getDataHoraInicio()) + "  |  " +
                valorOuPadrao(formatarNome(consulta.getIdDentista() != null ? consulta.getIdDentista().getUtilizador() : null)));
        subtitulo.getStyleClass().add("patient-history-subtitle");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(getTextoEstado(consulta.getStatus()));
        status.getStyleClass().addAll("agenda-status-pill", getClasseEstado(consulta.getStatus()));

        header.getChildren().addAll(titleBox, spacer, status);

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(10);
        grid.getColumnConstraints().addAll(
                criarColuna(120),
                criarColuna(240),
                criarColuna(120),
                criarColuna(240)
        );

        adicionarLinha(grid, 0, "Tipo", valorOuPadrao(consulta.getTipo()), "Data da marcacao", formatarData(consulta.getDataMarcacao()));
        adicionarLinha(grid, 1, "Estado", getTextoEstado(consulta.getStatus()), "Dentista", valorOuPadrao(formatarNome(
                consulta.getIdDentista() != null ? consulta.getIdDentista().getUtilizador() : null
        )));

        if (consulta.getStatus() == EstadoConsulta.CANCELADA) {
            adicionarLinha(grid, 2, "Cancelamento", formatarData(consulta.getDataCancelamento()), "Motivo", valorOuPadrao(consulta.getMotivoCancelamento()));
        } else if (consulta.getObservacoes() != null && !consulta.getObservacoes().isBlank()) {
            Label legenda = new Label("Observacoes");
            legenda.getStyleClass().add("patient-history-label");
            Label valor = new Label(consulta.getObservacoes().trim());
            valor.getStyleClass().add("patient-history-value");
            valor.setWrapText(true);
            grid.add(legenda, 0, 2);
            grid.add(valor, 1, 2, 3, 1);
        }

        card.getChildren().addAll(header, grid);
        return card;
    }

    private ColumnConstraints criarColuna(double largura) {
        ColumnConstraints coluna = new ColumnConstraints();
        coluna.setMinWidth(largura);
        coluna.setPrefWidth(largura);
        coluna.setHgrow(Priority.SOMETIMES);
        return coluna;
    }

    private void adicionarLinha(GridPane grid, int row, String rotuloA, String valorA, String rotuloB, String valorB) {
        Label labelA = new Label(rotuloA);
        labelA.getStyleClass().add("patient-history-label");
        Label valueA = new Label(valorOuPadrao(valorA));
        valueA.getStyleClass().add("patient-history-value");
        valueA.setWrapText(true);

        Label labelB = new Label(rotuloB);
        labelB.getStyleClass().add("patient-history-label");
        Label valueB = new Label(valorOuPadrao(valorB));
        valueB.getStyleClass().add("patient-history-value");
        valueB.setWrapText(true);

        grid.add(labelA, 0, row);
        grid.add(valueA, 1, row);
        grid.add(labelB, 2, row);
        grid.add(valueB, 3, row);
    }

    private String obterResumoSeguro() {
        return pacientexSeguroService.listarTodos().stream()
                .filter(relacao -> relacao.getIdUtilizador() != null && Objects.equals(relacao.getIdUtilizador().getId(), pacienteAtual.getId()))
                .sorted(Comparator.comparing(PacientexSeguro::getDataFimCobertura,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(relacao -> {
                    String nome = relacao.getIdSeguro() != null ? relacao.getIdSeguro().getNomeSeguro() : null;
                    String apolice = relacao.getNumeroApolice();
                    if (apolice == null || apolice.isBlank()) {
                        return valorOuPadrao(nome);
                    }
                    return valorOuPadrao(nome) + "  |  Apolice " + apolice;
                })
                .findFirst()
                .orElse("Sem seguro associado");
    }

    private void validarFormulario() {
        if (txtPrimeiroNome.getText() == null || txtPrimeiroNome.getText().isBlank()) {
            throw new RuntimeException("Primeiro nome e obrigatorio.");
        }
        if (txtUltimoNome.getText() == null || txtUltimoNome.getText().isBlank()) {
            throw new RuntimeException("Ultimo nome e obrigatorio.");
        }
        if (txtNif.getText() == null || !txtNif.getText().trim().matches("\\d{9}")) {
            throw new RuntimeException("NIF deve conter exatamente 9 digitos.");
        }
        if (txtEmail.getText() == null || txtEmail.getText().isBlank() || !txtEmail.getText().contains("@")) {
            throw new RuntimeException("Email invalido.");
        }
        if (txtTelemovel.getText() != null && !txtTelemovel.getText().isBlank()
                && !txtTelemovel.getText().trim().matches("\\d{9}")) {
            throw new RuntimeException("Telemovel deve conter exatamente 9 digitos.");
        }
        if (txtTelefone.getText() != null && !txtTelefone.getText().isBlank()
                && !txtTelefone.getText().trim().matches("\\d{9}")) {
            throw new RuntimeException("Telefone deve conter exatamente 9 digitos.");
        }
        if (cbStatus.getValue() == null || cbStatus.getValue().isBlank()) {
            throw new RuntimeException("Status do paciente e obrigatorio.");
        }
    }

    private void atualizarModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;

        List<Control> campos = List.of(
                txtPrimeiroNome,
                txtUltimoNome,
                txtNif,
                txtEmail,
                txtTelemovel,
                txtTelefone,
                dpNascimento,
                txtRua,
                txtNumeroPorta,
                cbStatus
        );

        for (Control campo : campos) {
            campo.setDisable(!modoEdicao);
            campo.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("readonly"), !modoEdicao);
        }

        btnGuardar.setVisible(modoEdicao);
        btnGuardar.setManaged(modoEdicao);
        btnCancelar.setVisible(modoEdicao);
        btnCancelar.setManaged(modoEdicao);
        btnEditarPerfil.setDisable(modoEdicao);
    }

    private String resolverProcedimento(Consulta consulta) {
        if (consulta == null) {
            return null;
        }

        if (consulta.getObservacoes() != null) {
            for (String linha : consulta.getObservacoes().split("\\R")) {
                if (linha.startsWith("Procedimento:")) {
                    return linha.substring("Procedimento:".length()).trim();
                }
            }
        }
        return consulta.getTipo();
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

    private String calcularIniciais(Utilizador utilizador) {
        if (utilizador == null) {
            return "--";
        }

        String primeiroNome = valorVazio(utilizador.getPrimeiroNome());
        String ultimoNome = valorVazio(utilizador.getUltimoNome());
        String inicialA = primeiroNome.isBlank() ? "" : primeiroNome.substring(0, 1).toUpperCase();
        String inicialB = ultimoNome.isBlank() ? "" : ultimoNome.substring(0, 1).toUpperCase();
        String iniciais = (inicialA + inicialB).trim();
        return iniciais.isBlank() ? "--" : iniciais;
    }

    private String getTextoEstado(EstadoConsulta estado) {
        if (estado == null) {
            return "-";
        }

        return switch (estado) {
            case CONCLUIDA -> "Concluida";
            case FATURADA -> "Faturada";
            case EM_CONSULTA -> "Em consulta";
            case EM_ESPERA -> "Em espera";
            case CONFIRMADA -> "Confirmada";
            case AGENDADA -> "Agendada";
            case CANCELADA -> "Cancelada";
            case FALTA -> "Falta";
            case PENDENTE -> "Pendente";
            case EM_ATENDIMENTO -> "Em atendimento";
        };
    }

    private String getClasseEstado(EstadoConsulta estado) {
        if (estado == null) {
            return "agenda-status-default";
        }

        return switch (estado) {
            case CONCLUIDA -> "agenda-status-concluido";
            case FATURADA -> "agenda-status-confirmado";
            case EM_CONSULTA -> "agenda-status-em-consulta";
            case EM_ESPERA -> "agenda-status-sala-espera";
            case CONFIRMADA -> "agenda-status-confirmado";
            case AGENDADA -> "agenda-status-agendado";
            default -> "agenda-status-default";
        };
    }

    private String formatarData(LocalDate data) {
        return data == null ? "-" : data.format(DATE_FORMATTER);
    }

    private String formatarDataHora(Instant instante) {
        if (instante == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER.format(instante.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    private String primeiroValorPreenchido(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }

    private String valorOuPadrao(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String valorVazio(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String valorOuNull(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    @FXML
    private void voltarPacientes() throws IOException {
        SceneManager.trocarTela("/fxml/pacientes.fxml", "/css/dashboard-style.css");
    }

    @FXML
    private void abrirAgenda() throws IOException {
        SceneManager.trocarTela("/fxml/Agenda.fxml", "/css/dashboard-style.css");
    }

    @FXML
    private void abrirPacientes() throws IOException {
        SceneManager.trocarTela("/fxml/pacientes.fxml", "/css/dashboard-style.css");
    }

    @FXML
    private void abrirFaturacao() throws IOException {
        SceneManager.trocarTela("/fxml/payment-view.fxml", "/css/payment-style.css");
    }

    @FXML
    private void fazerLogout() throws IOException {
        SessionContext.limparSessao();
        SceneManager.trocarTelaMaximizado("/fxml/login-view.fxml", "/css/login-style.css");
    }
    

    private void aplicarStylesheet(Scene scene, String fxmlPath) {
        String cssPath = switch (fxmlPath) {
            case "/fxml/Agenda.fxml", "/fxml/pacientes.fxml", "/fxml/PacienteView.fxml" -> "/css/dashboard-style.css";
            case "/fxml/payment-view.fxml" -> "/css/payment-style.css";
            case "/fxml/login-view.fxml" -> "/css/login-style.css";
            default -> null;
        };

        if (cssPath == null) {
            return;
        }

        var cssResource = getClass().getResource(cssPath);
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }
    }

    private void mostrarInformacao(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacao");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem != null && !mensagem.isBlank() ? mensagem : "Nao foi possivel concluir a operacao.");
        alert.showAndWait();
    }
}
