package controller;

import javafx.fxml.FXML;
import model.Recepcionista;
import model.Utilizador;
import org.springframework.stereotype.Component;

@Component
public class MenuController {
    
    @FXML
    public void initialize() {
        // Inicialização do menu
    }
    
    public void setDadosLogin(Utilizador utilizadorLogado, Recepcionista recepcionistaLogado) {
        // Configurar dados de login do usuário
    }
}
