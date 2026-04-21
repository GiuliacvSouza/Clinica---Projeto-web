package controller;

import bll.PacienteService;
import org.springframework.stereotype.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component  //Spring gere este controller
public class MainController {

    private final PacienteService pacienteService;

    public MainController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @FXML
    private Label lblTotal;

    @FXML
    public void initialize() {
        lblTotal.setText("Pacientes: " +
                pacienteService.listarTodos().size());
    }
}