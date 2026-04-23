package controller;

import app.MainFX;
import app.SceneManager;
import app.SessionContext;
import bll.PacienteService;
import bll.PacientexSeguroService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Paciente;
import model.Utilizador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PacientesController {

    @FXML private Label nomeUtilizador;
    @FXML private Label lblTotalPacientes;
    @FXML private TextField txtPesquisa;
    @FXML private TableView<PacienteLinha> tblPacientes;
    @FXML private TableColumn<PacienteLinha, String> colNome;
    @FXML private TableColumn<PacienteLinha, String> colNif;
    @FXML private TableColumn<PacienteLinha, String> colSeguro;
    @FXML private TableColumn<PacienteLinha, String> colUltimaVisita;
    @FXML private TableColumn<PacienteLinha, String> colContacto;
    @FXML private TableColumn<PacienteLinha, String> colAcoes;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private PacientexSeguroService pacientexSeguroService;

    private FilteredList<PacienteLinha> pacientesFiltrados;
    private boolean listenerPesquisaConfigurado;

    @FXML
    public void initialize() {
        Utilizador utilizadorLogado = SessionContext.getUtilizadorLogado();
        if (utilizadorLogado != null && nomeUtilizador != null) {
            nomeUtilizador.setText(utilizadorLogado.getPrimeiroNome() + " " + utilizadorLogado.getUltimoNome());
        }

        configurarTabela();
        carregarPacientes();
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colNif.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNif()));
        colSeguro.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSeguro()));
        colUltimaVisita.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUltimaVisita()));
        colContacto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContacto()));
        colAcoes.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("ver"));

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnVer = new Button("Ver ficha");

            {
                btnVer.getStyleClass().add("agenda-action-button");
                btnVer.setOnAction(event -> {
                    PacienteLinha linha = getTableView().getItems().get(getIndex());
                    mostrarAlerta(linha.getResumo());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnVer);
            }
        });
    }

    private void carregarPacientes() {
        List<Paciente> pacientes = pacienteService.listarTodos();
        Map<Integer, String> seguroPorPaciente = carregarSegurosPorPaciente();

        ObservableList<PacienteLinha> linhas = FXCollections.observableArrayList(
                pacientes.stream()
                        .map(paciente -> toPacienteLinha(paciente, seguroPorPaciente.get(paciente.getId())))
                        .sorted(Comparator.comparing(PacienteLinha::getNome, String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );

        pacientesFiltrados = new FilteredList<>(linhas, paciente -> true);
        tblPacientes.setItems(pacientesFiltrados);

        if (txtPesquisa != null && !listenerPesquisaConfigurado) {
            txtPesquisa.textProperty().addListener((obs, oldValue, newValue) ->
                    pacientesFiltrados.setPredicate(paciente -> correspondePesquisa(paciente, newValue)));
            listenerPesquisaConfigurado = true;
        }

        if (txtPesquisa != null) {
            pacientesFiltrados.setPredicate(paciente -> correspondePesquisa(paciente, txtPesquisa.getText()));
        }

        atualizarTotalPacientes(linhas.size());
    }

    private Map<Integer, String> carregarSegurosPorPaciente() {
        return pacientexSeguroService.listarTodos().stream()
                .filter(ps -> ps.getIdUtilizador() != null && ps.getIdUtilizador().getId() != null)
                .filter(ps -> ps.getIdSeguro() != null)
                .collect(Collectors.toMap(
                        ps -> ps.getIdUtilizador().getId(),
                        ps -> valorOuPadrao(ps.getIdSeguro().getNomeSeguro()),
                        (atual, novo) -> atual
                ));
    }

    private PacienteLinha toPacienteLinha(Paciente paciente, String seguro) {
        Utilizador utilizador = paciente.getUtilizador();
        String nome = formatarNome(utilizador);
        String nif = utilizador != null ? valorOuPadrao(utilizador.getNif()) : "-";
        String contacto = utilizador != null ? primeiroValorPreenchido(utilizador.getTelemovel(), utilizador.getTelefone(), utilizador.getEmail()) : "-";
        String ultimaVisita = paciente.getDataRegisto() != null ? paciente.getDataRegisto().toString() : "-";

        return new PacienteLinha(
                paciente.getId(),
                valorOuPadrao(nome),
                nif,
                valorOuPadrao(seguro),
                ultimaVisita,
                contacto
        );
    }

    private boolean correspondePesquisa(PacienteLinha paciente, String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return true;
        }

        String termo = filtro.trim().toLowerCase();
        return paciente.getNome().toLowerCase().contains(termo)
                || paciente.getNif().toLowerCase().contains(termo)
                || paciente.getContacto().toLowerCase().contains(termo)
                || paciente.getSeguro().toLowerCase().contains(termo);
    }

    private void atualizarTotalPacientes(int total) {
        if (lblTotalPacientes != null) {
            lblTotalPacientes.setText(total + " pacientes");
        }
    }

    private String primeiroValorPreenchido(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return "-";
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

    private String valorOuPadrao(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    @FXML
    private void abrirAgenda() throws IOException {
        SceneManager.trocarTela("/fxml/Agenda.fxml", "/css/dashboard-style.css");
    }

    @FXML
    private void abrirPacientes() {
        // Pagina atual.
    }

    @FXML
    private void abrirModalAdicionarPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/registo-paciente-modal.fxml"));
            if (MainFX.getSpringContext() != null) {
                loader.setControllerFactory(MainFX.getSpringContext()::getBean);
            }

            Parent root = loader.load();
            RegistoPacienteController controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(nomeUtilizador.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.setTitle("Registo de Paciente");

            Scene scene = new Scene(root);
            var css = getClass().getResource("/css/dashboard-style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            controller.setStage(modalStage);
            modalStage.setScene(scene);
            modalStage.showAndWait();

            if (controller.isSaved()) {
                carregarPacientes();
            }
        } catch (Exception ex) {
            Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
            mostrarAlerta(causa.getMessage());
        }
    }

    @FXML
    private void abrirFaturacao() throws IOException {
        SceneManager.trocarTela("/fxml/payment-view.fxml", "/css/dashboard-style.css");
    }

    @FXML
    private void fazerLogout() throws IOException {
        SessionContext.limparSessao();
        SceneManager.trocarTelaMaximizado("/fxml/login-view.fxml", "/css/login-style.css");
    }



    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informacao");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static class PacienteLinha {
        private final Integer id;
        private final String nome;
        private final String nif;
        private final String seguro;
        private final String ultimaVisita;
        private final String contacto;

        public PacienteLinha(Integer id, String nome, String nif, String seguro, String ultimaVisita, String contacto) {
            this.id = id;
            this.nome = nome;
            this.nif = nif;
            this.seguro = seguro;
            this.ultimaVisita = ultimaVisita;
            this.contacto = contacto;
        }

        public Integer getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getNif() {
            return nif;
        }

        public String getSeguro() {
            return seguro;
        }

        public String getUltimaVisita() {
            return ultimaVisita;
        }

        public String getContacto() {
            return contacto;
        }

        public String getResumo() {
            return "Nome: " + nome + "\n"
                    + "NIF: " + nif + "\n"
                    + "Seguro: " + seguro + "\n"
                    + "Ultima visita: " + ultimaVisita + "\n"
                    + "Contacto: " + contacto;
        }
    }
}
