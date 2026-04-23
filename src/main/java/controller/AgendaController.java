package controller;

import app.MainFX;
import app.SessionContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Utilizador;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AgendaController {

    @FXML private Label nomeUtilizador;

    @FXML
    public void initialize() {
        Utilizador utilizadorLogado = SessionContext.getUtilizadorLogado();
        if (utilizadorLogado != null && nomeUtilizador != null) {
            nomeUtilizador.setText(utilizadorLogado.getPrimeiroNome() + " " + utilizadorLogado.getUltimoNome());
        }
    }

    @FXML
    private void abrirAgenda() {
        // Pagina atual.
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
}
