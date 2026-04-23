package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gerenciador centralizado de mudanças de cena (trocar de tela).
 * Mantém uniformidade no comportamento de tamanho e estado da janela.
 */
public class SceneManager {

    private static Stage mainStage;

    private SceneManager() {
        // Utilitário estático - não instanciar
    }

    /**
     * Define o Stage principal da aplicação.
     * Deve ser chamado uma vez na inicialização, geralmente no LoginController.
     */
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Troca a cena mantendo uniformidade: sempre maximizado/fullscreen.
     *
     * @param fxmlPath     Caminho do FXML a carregar (ex: "/fxml/Agenda.fxml")
     * @param cssPath      Caminho do CSS a aplicar (ex: "/css/dashboard-style.css")
     */
    public static void trocarTela(String fxmlPath, String cssPath) throws IOException {
        if (mainStage == null) {
            throw new RuntimeException("SceneManager nao foi inicializado. Chame setMainStage() primeiro.");
        }

        var resource = SceneManager.class.getResource(fxmlPath);
        if (resource == null) {
            throw new RuntimeException("FXML nao encontrado: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        if (MainFX.getSpringContext() != null) {
            loader.setControllerFactory(MainFX.getSpringContext()::getBean);
        }

        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Aplicar CSS se fornecido
        if (cssPath != null && !cssPath.isEmpty()) {
            var cssResource = SceneManager.class.getResource(cssPath);
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
        }

        // Manter estado de maximizado/fullscreen e dimensões
        boolean eraMaximizado = mainStage.isMaximized();
        boolean eraFullScreen = mainStage.isFullScreen();
        double largura = mainStage.getWidth();
        double altura = mainStage.getHeight();
        double x = mainStage.getX();
        double y = mainStage.getY();

        mainStage.setScene(scene);

        // Restaurar estado anterior (máximizado/fullscreen)
        if (eraFullScreen) {
            mainStage.setFullScreen(true);
        } else if (eraMaximizado) {
            mainStage.setMaximized(true);
        } else {
            // Se não estava maximizado, mantém a mesma posição e tamanho
            mainStage.setWidth(largura);
            mainStage.setHeight(altura);
            mainStage.setX(x);
            mainStage.setY(y);
        }

        mainStage.show();
    }

    /**
     * Versão simplificada: apenas troca cena (sem controle de CSS).
     */
    public static void trocarTela(String fxmlPath) throws IOException {
        trocarTela(fxmlPath, null);
    }

    /**
     * Retorna o Stage principal.
     */
    public static Stage getMainStage() {
        return mainStage;
    }

    /**
     * Define a cena e maximiza a janela (comportamento padrão das telas do app).
     */
    public static void trocarTelaMaximizado(String fxmlPath, String cssPath) throws IOException {
        if (mainStage == null) {
            throw new RuntimeException("SceneManager nao foi inicializado.");
        }

        var resource = SceneManager.class.getResource(fxmlPath);
        if (resource == null) {
            throw new RuntimeException("FXML nao encontrado: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        if (MainFX.getSpringContext() != null) {
            loader.setControllerFactory(MainFX.getSpringContext()::getBean);
        }

        Parent root = loader.load();
        Scene scene = new Scene(root);

        if (cssPath != null && !cssPath.isEmpty()) {
            var cssResource = SceneManager.class.getResource(cssPath);
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
        }

        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        mainStage.show();
    }
}
