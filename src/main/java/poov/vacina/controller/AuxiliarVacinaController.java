package poov.vacina.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AuxiliarVacinaController {

    @FXML
    private TextField inputCodigo;

    @FXML
    private TextArea inputDesc;

    @FXML
    private TextField inputNome;

    @FXML
    private Label labelCodigo;

    @FXML
    private Label labelDesc;

    @FXML
    private Label labelNome;

    private boolean validarCampos() {
        return !inputCodigo.getText().isEmpty() &&
                !inputDesc.getText().isEmpty() &&
                !inputNome.getText().isEmpty();
    }

    @FXML
    void buttonCancel(ActionEvent event) {
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void buttonConfirm(ActionEvent event) {

        if (validarCampos()) {
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Um dos campos está vazio");
            alert.showAndWait();
            alert.setTitle("Aviso");
            alert.setHeaderText("Preencha todos os campos");
        }

    }

}
