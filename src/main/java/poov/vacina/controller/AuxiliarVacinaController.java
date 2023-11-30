package poov.vacina.controller;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import poov.vacina.model.Vacina;
import poov.vacina.model.dao.ConexaoFactoryPostgreSQL;
import poov.vacina.model.dao.VacinaDAO;
import poov.vacina.model.dao.core.ConnectionFactory;
import poov.vacina.model.dao.core.DAOFactory;

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

    private boolean edit;
    private Vacina vacina;

    private DAOFactory factory;

    public AuxiliarVacinaController() {
        ConnectionFactory conexaoFactory = new ConexaoFactoryPostgreSQL("localhost:5432/POOV", "postgres",
                "lockspick12");
        factory = new DAOFactory(conexaoFactory);
    }

    public void recebendo(Vacina vacina, boolean edit) {
        this.vacina = vacina;
        this.edit = edit;
        if (edit) {
            inputCodigo.setText(String.valueOf(vacina.getCodigo()));
            inputNome.setText(vacina.getNome());
            inputDesc.setText(vacina.getDescricao());
        }
    }

    private boolean validarCampos() {
        return !inputDesc.getText().isEmpty() &&
                !inputNome.getText().isEmpty();
    }

    private void limparCampos() {
        inputCodigo.setText("");
        inputDesc.setText("");
        inputNome.setText("");
    }

    @FXML
    void buttonCancel(ActionEvent event) {
        limparCampos();
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void buttonConfirm(ActionEvent event) {

        if (validarCampos()) {
            vacina.setNome(inputNome.getText());
            vacina.setDescricao(inputDesc.getText());

            try {
                factory.abrirConexao();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            VacinaDAO dao = factory.getDAO(VacinaDAO.class);

            if (edit) {
                dao.update(vacina);
            } else {
                System.out.println(vacina);
                // dao.create(vacina);
            }

            factory.fecharConexao();
            limparCampos();
            ((Button) event.getSource()).getScene().getWindow().hide();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Um dos campos está vazio");
            alert.showAndWait();
            alert.setTitle("Aviso");
            alert.setHeaderText("Preencha todos os campos");
        }

    }

}
