package poov.vacina.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import poov.vacina.model.Situacao;
import poov.vacina.model.Vacina;
import poov.vacina.model.dao.ConexaoFactoryPostgreSQL;
import poov.vacina.model.dao.VacinaDAO;
import poov.vacina.model.dao.core.ConnectionFactory;
import poov.vacina.model.dao.core.DAOFactory;

public class PrincipalVacinaController implements Initializable {

    @FXML
    private TableColumn<Vacina, String> ColDesc;

    @FXML
    private TableColumn<Vacina, Long> colCodigo;

    @FXML
    private TableColumn<Vacina, String> colNome;

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

    @FXML
    private TableView<Vacina> principalTable;

    private Stage stageAuxiliar;
    private AuxiliarVacinaController controllerAuxiliar;
    private Vacina vacina;

    List<Vacina> vacinas = null;

    private DAOFactory factory;

    public PrincipalVacinaController() {
        ConnectionFactory conexaoFactory = new ConexaoFactoryPostgreSQL("localhost:5432/POOV", "postgres",
                "lockspick12");
        factory = new DAOFactory(conexaoFactory);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poov/vacina/auxiliarVacina.fxml"));
        Parent root;
        try {
            root = loader.load();
            Scene scene = new Scene(root);
            stageAuxiliar = new Stage();
            stageAuxiliar.getIcons().add(new Image(getClass().getResourceAsStream("/poov/images/java.png")));
            stageAuxiliar.setScene(scene);
            controllerAuxiliar = loader.getController();

            factory.abrirConexao();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        VacinaDAO dao = factory.getDAO(VacinaDAO.class);
        vacinas = dao.findAll();

        factory.fecharConexao();

        colCodigo.setCellValueFactory(new PropertyValueFactory<Vacina, Long>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<Vacina, String>("nome"));
        ColDesc.setCellValueFactory(new PropertyValueFactory<Vacina, String>("descricao"));
        principalTable.setPlaceholder(new Label("Nenhuma vacina cadastrada"));
        principalTable.getItems().addAll(vacinas);
    }

    @FXML
    void ButtonEditar(ActionEvent event) throws SQLException {
        if (principalTable.getSelectionModel().getSelectedItem() != null) {
            vacina = principalTable.getSelectionModel().getSelectedItem();
            controllerAuxiliar.recebendo(vacina, true);
            stageAuxiliar.setTitle("Editar de Vacina");
            stageAuxiliar.showAndWait();

            principalTable.getItems().clear();
            try {
                factory.abrirConexao();
                VacinaDAO dao = factory.getDAO(VacinaDAO.class);
                vacinas = dao.findAll();
                principalTable.getItems().addAll(vacinas);
            } finally {
                factory.fecharConexao();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Selecione uma Vacina");
            alert.setTitle("Edição");
            alert.setHeaderText("Sem Vacina para Editar");
            alert.showAndWait();
        }
    }

    @FXML
    void ButtonIncluir(ActionEvent event) throws SQLException {
        controllerAuxiliar.recebendo(new Vacina(), false);
        stageAuxiliar.setTitle("Incluir de Vacina");
        stageAuxiliar.showAndWait();

        principalTable.getItems().clear();
        try {
            factory.abrirConexao();
            VacinaDAO dao = factory.getDAO(VacinaDAO.class);
            vacinas = dao.findAll();
            principalTable.getItems().addAll(vacinas);
        } finally {
            factory.fecharConexao();
        }
    }

    @FXML
    void ButtonRemover(ActionEvent event) throws SQLException {
        if (principalTable.getSelectionModel().getSelectedItem() != null) {
            vacina = principalTable.getSelectionModel().getSelectedItem();
            ButtonType sim = new ButtonType("Sim", ButtonBar.ButtonData.OK_DONE);
            ButtonType nao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(AlertType.CONFIRMATION, "Deseja Mesmo Remover a Vacina?", sim, nao);
            alert.setTitle("Remoção");
            alert.setHeaderText("Vacina: " + vacina.getNome());

            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(sim)) {
                try {
                    factory.abrirConexao();
                    VacinaDAO dao = factory.getDAO(VacinaDAO.class);
                    vacina.setSituacao(Situacao.INATIVO);
                    dao.update(vacina);
                    vacinas = dao.findAll();
                    principalTable.getItems().clear();
                    principalTable.getItems().addAll(vacinas);

                } finally {
                    factory.fecharConexao();
                }
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Selecione uma Vacina");
            alert.setTitle("Remoção");
            alert.setHeaderText("Sem Vacina para Remover");
            alert.showAndWait();
        }
    }

    @FXML
    void buttonPesquisar(ActionEvent event) {
        if (inputCodigo.getText().isEmpty() && inputNome.getText().isEmpty() && inputDesc.getText().isEmpty()) {
            principalTable.getItems().clear();
            principalTable.getItems().addAll(vacinas);
        } else {

            if (!inputCodigo.getText().isEmpty()) {
                principalTable.getItems().clear();
                for (Vacina vacina : vacinas) {
                    if (vacina.getCodigo() == Long.parseLong(inputCodigo.getText())) {
                        principalTable.getItems().add(vacina);
                    }
                }
            }
            if (!inputNome.getText().isEmpty()) {
                principalTable.getItems().clear();
                for (Vacina vacina : vacinas) {
                    if (vacina.getNome().equals(inputNome.getText())) {
                        principalTable.getItems().add(vacina);
                    }
                }
            }
            if (!inputDesc.getText().isEmpty()) {
                principalTable.getItems().clear();
                for (Vacina vacina : vacinas) {
                    if (vacina.getDescricao().equals(inputDesc.getText())) {
                        principalTable.getItems().add(vacina);
                    }
                }

            }
        }
    }

}
