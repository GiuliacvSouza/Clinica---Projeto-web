package controller;

import app.MainFX;
import app.SessionContext;
import bll.ConsultaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Consulta;
import model.Paciente;
import model.Utilizador;
import model.dto.ConsultaAgendadaDTO;
import model.enums.EstadoConsulta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Component
public class AgendaController {

    @FXML private Label nomeUtilizador;
    @FXML private TableView<ConsultaAgendadaDTO> tblConsultas;
    @FXML private TableColumn<ConsultaAgendadaDTO, LocalDateTime> colHora;
    @FXML private TableColumn<ConsultaAgendadaDTO, String> colPaciente;
    @FXML private TableColumn<ConsultaAgendadaDTO, String> colDentista;
    @FXML private TableColumn<ConsultaAgendadaDTO, String> colProcedimento;
    @FXML private TableColumn<ConsultaAgendadaDTO, EstadoConsulta> colEstado;
    @FXML private TableColumn<ConsultaAgendadaDTO, String> colAcoes;

    @FXML private Button btnTodas;
    @FXML private Button btnPendentes;
    @FXML private Button btnEmEspera;
    @FXML private Button btnEmConsulta;
    @FXML private Button btnConcluidas;

    @Autowired
    private ConsultaService consultaService;

    private EstadoConsulta filtroAtual = null;
    private ObservableList<ConsultaAgendadaDTO> consultasCarregadas;

    @FXML
    public void initialize() {
        Utilizador utilizadorLogado = SessionContext.getUtilizadorLogado();
        if (utilizadorLogado != null && nomeUtilizador != null) {
            nomeUtilizador.setText(utilizadorLogado.getPrimeiroNome() + " " + utilizadorLogado.getUltimoNome());
        }

        configurarTabela();
        carregarConsultas();
    }

    private void configurarTabela() {
        colHora.setCellValueFactory(cellData -> {
            LocalDateTime dataHora = LocalDateTime.ofInstant(
                    cellData.getValue().getDataHoraInicio(),
                    ZoneId.systemDefault()
            );
            return new javafx.beans.property.SimpleObjectProperty<>(dataHora);
        });

        colHora.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        });

        colPaciente.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNomePaciente() != null ? cellData.getValue().getNomePaciente() : "Desconhecido"
                )
        );

        colDentista.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getNomeDentista() != null ? cellData.getValue().getNomeDentista() : "Desconhecido"
                )
        );

        colProcedimento.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProcedimento()));

        colEstado.setCellValueFactory(new PropertyValueFactory<>("status"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            private final Label statusLabel = new Label();
            private final StackPane wrapper = new StackPane(statusLabel);

            {
                statusLabel.getStyleClass().add("agenda-status-pill");
                wrapper.setPrefHeight(44);
            }

            @Override
            protected void updateItem(EstadoConsulta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                statusLabel.setText(getTextoEstado(item));
                statusLabel.getStyleClass().setAll("agenda-status-pill", getClasseEstado(item));
                setText(null);
                setGraphic(wrapper);
            }
        });

        colAcoes.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnVerFicha = new Button("Ver ficha do paciente");
            private final Button btnAcaoPrimaria = new Button();
            private final Button btnAcaoSecundaria = new Button();
            private final Button btnCancelar = new Button("Cancelar");
            private final HBox actionsBox = new HBox(10, btnVerFicha, btnAcaoPrimaria, btnAcaoSecundaria, btnCancelar);

            {
                actionsBox.getStyleClass().add("agenda-actions-box");
                btnVerFicha.getStyleClass().add("agenda-action-button");
                btnAcaoPrimaria.getStyleClass().add("agenda-action-button");
                btnAcaoSecundaria.getStyleClass().add("agenda-action-button");
                btnCancelar.getStyleClass().add("agenda-link-button");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                ConsultaAgendadaDTO consulta = getTableView().getItems().get(getIndex());
                configurarBotoesAcao(consulta);
                setGraphic(actionsBox);
            }

            private void configurarBotoesAcao(ConsultaAgendadaDTO consulta) {
                EstadoConsulta status = consulta.getStatus();
                btnVerFicha.setOnAction(e -> abrirFichaPaciente(consulta));

                btnAcaoPrimaria.setVisible(false);
                btnAcaoPrimaria.setManaged(false);
                btnAcaoSecundaria.setVisible(false);
                btnAcaoSecundaria.setManaged(false);
                btnCancelar.setVisible(false);
                btnCancelar.setManaged(false);

                if (status == EstadoConsulta.AGENDADA) {
                    btnAcaoPrimaria.setText("Confirmar consulta");
                    btnAcaoPrimaria.setVisible(true);
                    btnAcaoPrimaria.setManaged(true);
                    btnAcaoPrimaria.setOnAction(e -> executarAcaoComFeedback(
                            () -> consultaService.confirmarConsulta(consulta.getIdConsulta()),
                            "Consulta confirmada com sucesso."
                    ));

                    btnAcaoSecundaria.setText("Reagendar");
                    btnAcaoSecundaria.setVisible(true);
                    btnAcaoSecundaria.setManaged(true);
                    btnAcaoSecundaria.setOnAction(e -> reagendarConsulta(consulta));

                    btnCancelar.setVisible(true);
                    btnCancelar.setManaged(true);
                    btnCancelar.setOnAction(e -> executarAcaoComFeedback(
                            () -> consultaService.cancelar(consulta.getIdConsulta()),
                            "Consulta cancelada com sucesso."
                    ));
                    return;
                }

                if (status == EstadoConsulta.CONFIRMADA) {
                    btnAcaoPrimaria.setText("Marcar chegada");
                    btnAcaoPrimaria.setVisible(true);
                    btnAcaoPrimaria.setManaged(true);
                    btnAcaoPrimaria.setOnAction(e -> executarAcaoComFeedback(
                            () -> consultaService.marcarChegada(consulta.getIdConsulta()),
                            "Consulta atualizada para Em espera."
                    ));
                    return;
                }

                if (status == EstadoConsulta.EM_ESPERA) {
                    btnAcaoPrimaria.setText("Iniciar consulta");
                    btnAcaoPrimaria.setVisible(true);
                    btnAcaoPrimaria.setManaged(true);
                    btnAcaoPrimaria.setOnAction(e -> executarAcaoComFeedback(
                            () -> consultaService.iniciarConsulta(consulta.getIdConsulta()),
                            "Consulta iniciada com sucesso."
                    ));
                    return;
                }

                if (status == EstadoConsulta.EM_CONSULTA) {
                    btnAcaoPrimaria.setText("Finalizar consulta");
                    btnAcaoPrimaria.setVisible(true);
                    btnAcaoPrimaria.setManaged(true);
                    btnAcaoPrimaria.setOnAction(e -> executarAcaoComFeedback(
                            () -> consultaService.finalizarConsulta(consulta.getIdConsulta()),
                            "Consulta finalizada com sucesso."
                    ));
                }
            }
        });
    }

    private void carregarConsultas() {
        try {
            List<ConsultaAgendadaDTO> consultas = filtroAtual == null
                    ? consultaService.listarTodasAgendadas()
                    : consultaService.listarPorStatusAgendadas(filtroAtual);

            consultasCarregadas = FXCollections.observableArrayList(consultas);
            tblConsultas.setItems(consultasCarregadas);
        } catch (Exception e) {
            System.err.println("[AGENDA] Erro ao carregar consultas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executarAcaoComFeedback(Runnable acao, String mensagemSucesso) {
        try {
            acao.run();
            carregarConsultas();
            mostrarAlerta(mensagemSucesso);
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void reagendarConsulta(ConsultaAgendadaDTO consultaDto) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Reagendar consulta");
        dialog.setHeaderText("Informe a nova data e hora da consulta.");

        ButtonType confirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmar, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        TextField horaField = new TextField();
        horaField.setPromptText("HH:mm");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.add(new Label("Data"), 0, 0);
        form.add(datePicker, 1, 0);
        form.add(new Label("Hora"), 0, 1);
        form.add(horaField, 1, 1);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(button -> {
            if (button != confirmar) {
                return null;
            }

            if (datePicker.getValue() == null || horaField.getText() == null || horaField.getText().isBlank()) {
                throw new IllegalArgumentException("Informe data e hora para reagendar.");
            }

            try {
                LocalTime hora = LocalTime.parse(horaField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                return LocalDateTime.of(datePicker.getValue(), hora);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Hora inválida. Use o formato HH:mm.");
            }
        });

        try {
            Optional<LocalDateTime> resultado = dialog.showAndWait();
            if (resultado.isEmpty()) {
                return;
            }

            Instant novaDataHora = resultado.get().atZone(ZoneId.systemDefault()).toInstant();
            executarAcaoComFeedback(
                    () -> consultaService.reagendar(consultaDto.getIdConsulta(), novaDataHora),
                    "Consulta reagendada com sucesso."
            );
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void abrirFichaPaciente(ConsultaAgendadaDTO consultaDto) {
        try {
            Consulta consulta = consultaService.buscarPorId(consultaDto.getIdConsulta());
            Paciente paciente = consulta.getIdPaciente();
            Utilizador utilizador = paciente != null ? paciente.getUtilizador() : null;

            if (utilizador == null) {
                mostrarErro("Paciente sem dados associados.");
                return;
            }

            String detalhes = montarFichaPaciente(consulta, paciente, utilizador);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ficha do paciente");
            alert.setHeaderText(utilizador.getPrimeiroNome() + " " + utilizador.getUltimoNome());
            alert.setContentText(detalhes);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private String montarFichaPaciente(Consulta consulta, Paciente paciente, Utilizador utilizador) {
        String dataNascimento = utilizador.getDataNascimento() != null ? utilizador.getDataNascimento().toString() : "-";
        String email = valorOuPadrao(utilizador.getEmail());
        String nif = valorOuPadrao(utilizador.getNif());
        String telefone = valorOuPadrao(utilizador.getTelefone());
        String telemovel = valorOuPadrao(utilizador.getTelemovel());
        String statusPaciente = paciente != null ? valorOuPadrao(paciente.getStatus()) : "-";
        String dentista = formatarNome(consulta.getIdDentista() != null ? consulta.getIdDentista().getUtilizador() : null);
        String dataConsulta = consulta.getDataHoraInicio() != null
                ? LocalDateTime.ofInstant(consulta.getDataHoraInicio(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "-";

        return "NIF: " + nif + "\n"
                + "Email: " + email + "\n"
                + "Telefone: " + telefone + "\n"
                + "Telemóvel: " + telemovel + "\n"
                + "Data de nascimento: " + dataNascimento + "\n"
                + "Status do paciente: " + statusPaciente + "\n"
                + "Consulta: " + valorOuPadrao(consulta.getTipo()) + "\n"
                + "Data/Hora: " + dataConsulta + "\n"
                + "Dentista: " + valorOuPadrao(dentista);
    }

    private String valorOuPadrao(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
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

    private String getTextoEstado(EstadoConsulta estado) {
        return switch (estado) {
            case CONCLUIDA -> "Concluído";
            case EM_CONSULTA -> "Em consulta";
            case EM_ESPERA -> "Sala de Espera";
            case CONFIRMADA -> "Confirmado";
            case AGENDADA -> "Agendado";
            default -> estado.getDescricao();
        };
    }

    private String getClasseEstado(EstadoConsulta estado) {
        return switch (estado) {
            case CONCLUIDA -> "agenda-status-concluido";
            case EM_CONSULTA -> "agenda-status-em-consulta";
            case EM_ESPERA -> "agenda-status-sala-espera";
            case CONFIRMADA -> "agenda-status-confirmado";
            case AGENDADA -> "agenda-status-agendado";
            default -> "agenda-status-default";
        };
    }

    @FXML
    private void filtrarTodas() {
        filtroAtual = null;
        atualizarEstilosFiltro(btnTodas);
        carregarConsultas();
    }

    @FXML
    private void filtrarPendentes() {
        filtroAtual = EstadoConsulta.PENDENTE;
        atualizarEstilosFiltro(btnPendentes);
        carregarConsultas();
    }

    @FXML
    private void filtrarEmEspera() {
        filtroAtual = EstadoConsulta.EM_ESPERA;
        atualizarEstilosFiltro(btnEmEspera);
        carregarConsultas();
    }

    @FXML
    private void filtrarEmConsulta() {
        filtroAtual = EstadoConsulta.EM_CONSULTA;
        atualizarEstilosFiltro(btnEmConsulta);
        carregarConsultas();
    }

    @FXML
    private void filtrarConcluidas() {
        filtroAtual = EstadoConsulta.CONCLUIDA;
        atualizarEstilosFiltro(btnConcluidas);
        carregarConsultas();
    }

    private void atualizarEstilosFiltro(Button botaoSelecionado) {
        btnTodas.getStyleClass().remove("filter-chip-active");
        btnPendentes.getStyleClass().remove("filter-chip-active");
        btnEmEspera.getStyleClass().remove("filter-chip-active");
        btnEmConsulta.getStyleClass().remove("filter-chip-active");
        btnConcluidas.getStyleClass().remove("filter-chip-active");
        botaoSelecionado.getStyleClass().add("filter-chip-active");
    }

    @FXML
    private void abrirAgenda() {
        // Página atual
    }

    @FXML
    private void abrirNovaMarcacao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/nova-marcacao.fxml"));
            if (MainFX.getSpringContext() != null) {
                loader.setControllerFactory(MainFX.getSpringContext()::getBean);
            }

            Parent root = loader.load();
            NovaMarcacaoController controller = loader.getController();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(nomeUtilizador.getScene().getWindow());
            modalStage.setResizable(false);
            modalStage.setTitle("Nova Marcação");

            Scene scene = new Scene(root);
            var css = getClass().getResource("/css/dashboard-style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            controller.setStage(modalStage);
            modalStage.setScene(scene);
            modalStage.showAndWait();

            if (controller.isSaved()) {
                carregarConsultas();
            }
        } catch (Exception ex) {
            Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
            mostrarErro(causa.getMessage());
        }
    }

    @FXML
    private void abrirPacientes() throws IOException {
        trocarTela("/fxml/pacientes.fxml");
    }

    @FXML
    private void abrirFaturacao() throws IOException {
        trocarTela("/fxml/payment-view.fxml");
    }

    @FXML
    private void fazerLogout() throws IOException {
        SessionContext.limparSessao();
        trocarTela("/fxml/login-view.fxml");
    }

    private void trocarTela(String fxmlPath) throws IOException {
        var resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            mostrarAlerta("A tela solicitada nao esta disponivel.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(resource);
        if (MainFX.getSpringContext() != null) {
            loader.setControllerFactory(MainFX.getSpringContext()::getBean);
        }

        Parent root = loader.load();
        Scene scene = new Scene(root);
        aplicarStylesheet(scene, fxmlPath);
        Stage stage = (Stage) nomeUtilizador.getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void aplicarStylesheet(Scene scene, String fxmlPath) {
        String cssPath = switch (fxmlPath) {
            case "/fxml/Agenda.fxml", "/fxml/pacientes.fxml" -> "/css/dashboard-style.css";
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

    private void mostrarAlerta(String mensagem) {
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
        alert.setContentText(mensagem != null && !mensagem.isBlank() ? mensagem : "Não foi possível concluir a operação.");
        alert.showAndWait();
    }
}
