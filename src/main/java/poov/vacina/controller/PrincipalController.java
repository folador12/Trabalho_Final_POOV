package poov.vacina.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import poov.vacina.model.Aplicacao;
import poov.vacina.model.Pessoa;
import poov.vacina.model.Situacao;
import poov.vacina.model.Vacina;
import poov.vacina.model.dao.AplicacaoDAO;
import poov.vacina.model.dao.ConexaoFactoryPostgreSQL;
import poov.vacina.model.dao.PessoaDAO;
import poov.vacina.model.dao.VacinaDAO;
import poov.vacina.model.dao.core.ConnectionFactory;
import poov.vacina.model.dao.core.DAOFactory;

public class PrincipalController implements Initializable {

    @FXML
    private TextField CPFPessoa;

    @FXML
    private TextField CodPessoa;

    @FXML
    private TextField CodVacina;

    @FXML
    private TableColumn<Pessoa, String> ColCPFPessoa;

    @FXML
    private TableColumn<Pessoa, Long> ColCodPessoa;

    @FXML
    private TableColumn<Vacina, Long> ColCodVacina;

    @FXML
    private TableColumn<Vacina, String> ColDescVacina;

    @FXML
    private TableColumn<Pessoa, LocalDate> ColNascPessoa;

    @FXML
    private TableColumn<Pessoa, String> ColNomePessoa;

    @FXML
    private TableColumn<Vacina, String> ColNomeVacina;

    @FXML
    private DatePicker DataPickerApartirDe;

    @FXML
    private DatePicker DataPickerAté;

    @FXML
    private TextArea DescricaoVacina;

    @FXML
    private TextField NomePessoa;

    @FXML
    private TextField NomeVacina;

    @FXML
    private TableView<Pessoa> TablePessoa;

    @FXML
    private TableView<Vacina> TableVacina;

    private Stage stageAuxiliar;
    private AuxiliarVacinaController controllerAuxiliar;
    private Vacina vacina;
    private Pessoa pessoa;

    List<Vacina> vacinas = null;
    List<Pessoa> pessoas = null;

    private DAOFactory factory;

    public PrincipalController() {
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
        PessoaDAO dao2 = factory.getDAO(PessoaDAO.class);
        vacinas = dao.findAll();
        pessoas = dao2.findAll();

        factory.fecharConexao();

        ColCodVacina.setCellValueFactory(new PropertyValueFactory<Vacina, Long>("codigo"));
        ColNomeVacina.setCellValueFactory(new PropertyValueFactory<Vacina, String>("nome"));
        ColDescVacina.setCellValueFactory(new PropertyValueFactory<Vacina, String>("descricao"));
        TableVacina.setPlaceholder(new Label("Nenhuma vacina cadastrada"));
        TableVacina.getItems().addAll(vacinas);

        ColCodPessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, Long>("codigo"));
        ColNomePessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, String>("nome"));
        ColCPFPessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, String>("cpf"));
        ColNascPessoa.setCellValueFactory(new PropertyValueFactory<Pessoa, LocalDate>("dataNascimento"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ColNascPessoa.setCellFactory(column -> new TableCell<Pessoa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });
        TablePessoa.setPlaceholder(new Label("Nenhuma pessoa cadastrada"));
        TablePessoa.getItems().addAll(pessoas);

        verificarCodigo(CodPessoa);
        verificarCodigo(CodVacina);
    }

    @FXML
    void CriarAplicacao(ActionEvent event) throws SQLException {
        if (TableVacina.getSelectionModel().getSelectedItem() != null
                && TablePessoa.getSelectionModel().getSelectedItem() != null) {
            vacina = TableVacina.getSelectionModel().getSelectedItem();
            pessoa = TablePessoa.getSelectionModel().getSelectedItem();
            System.out.println(vacina.getNome());
            System.out.println(pessoa.getNome());

            try {
                factory.abrirConexao();
                AplicacaoDAO dao = factory.getDAO(AplicacaoDAO.class);
                Aplicacao aplicacao = new Aplicacao();
                aplicacao.setPessoa(pessoa);
                aplicacao.setVacina(vacina);
                aplicacao.setData(LocalDate.now());
                aplicacao.setSituacao(Situacao.ATIVO);
                dao.create(aplicacao);
            } finally {
                factory.fecharConexao();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aplicação");
            alert.setHeaderText("Aplicação Criada com Sucesso");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Selecione uma Pessoa e/ou uma Vacina");
            alert.setTitle("Aplicação");
            alert.setHeaderText("Sem Pessoa e/ou Vacina para Aplicar");
            alert.showAndWait();
        }
    }

    @FXML
    void EditarVacina(ActionEvent event) throws SQLException {
        if (TableVacina.getSelectionModel().getSelectedItem() != null) {
            vacina = TableVacina.getSelectionModel().getSelectedItem();
            controllerAuxiliar.recebendo(vacina, true);
            stageAuxiliar.setTitle("Editar de Vacina");
            stageAuxiliar.showAndWait();

            TableVacina.getItems().clear();
            try {
                factory.abrirConexao();
                VacinaDAO dao = factory.getDAO(VacinaDAO.class);
                vacinas = dao.findAll();
                TableVacina.getItems().addAll(vacinas);
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
    void NovaVacina(ActionEvent event) throws SQLException {
        controllerAuxiliar.recebendo(new Vacina(), false);
        stageAuxiliar.setTitle("Incluir de Vacina");
        stageAuxiliar.showAndWait();

        TableVacina.getItems().clear();
        try {
            factory.abrirConexao();
            VacinaDAO dao = factory.getDAO(VacinaDAO.class);
            vacinas = dao.findAll();
            TableVacina.getItems().addAll(vacinas);
        } finally {
            factory.fecharConexao();
        }
    }

    @FXML
    void PesquisarPessoa(ActionEvent event) {
        List<Pessoa> resultados = new ArrayList<>(pessoas);

        if (!CodPessoa.getText().isEmpty()) {
            resultados.removeIf(pessoa -> pessoa.getCodigo() != Long.parseLong(CodPessoa.getText()));
        }

        if (!NomePessoa.getText().isEmpty()) {
            resultados.removeIf(pessoa -> !pessoa.getNome().equals(NomePessoa.getText()));
        }

        if (!CPFPessoa.getText().isEmpty()) {
            resultados.removeIf(pessoa -> !pessoa.getCpf().equals(CPFPessoa.getText()));
        }

        LocalDate dataApartirDe = DataPickerApartirDe.getValue();
        if (dataApartirDe != null) {
            resultados.removeIf(pessoa -> {
                LocalDate dataNascimento = pessoa.getDataNascimento();
                return dataNascimento.isBefore(dataApartirDe) || dataNascimento.isEqual(dataApartirDe);
            });
        }

        LocalDate dataAte = DataPickerAté.getValue();
        if (dataAte != null) {
            resultados.removeIf(pessoa -> {
                LocalDate dataNascimento = pessoa.getDataNascimento();
                return dataNascimento.isAfter(dataAte) || dataNascimento.isEqual(dataAte);
            });
        }

        TablePessoa.getItems().setAll(resultados);

    }

    @FXML
    void PesquisarVacina(ActionEvent event) {
        List<Vacina> resultados = new ArrayList<>(vacinas);

        if (!CodVacina.getText().isEmpty()) {
            resultados.removeIf(vacina -> vacina.getCodigo() != Long.parseLong(CodVacina.getText()));
        }

        if (!NomeVacina.getText().isEmpty()) {
            resultados.removeIf(vacina -> !vacina.getNome().equals(NomeVacina.getText()));
        }

        if (!DescricaoVacina.getText().isEmpty()) {
            resultados.removeIf(vacina -> !vacina.getDescricao().equals(DescricaoVacina.getText()));
        }

        TableVacina.getItems().setAll(resultados);
    }

    @FXML
    void RemoverVacina(ActionEvent event) throws SQLException {
        if (TableVacina.getSelectionModel().getSelectedItem() != null) {
            vacina = TableVacina.getSelectionModel().getSelectedItem();
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
                    TableVacina.getItems().clear();
                    TableVacina.getItems().addAll(vacinas);

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

    private void verificarCodigo(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                // If the new input is not numeric, replace it with an empty string
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

}
