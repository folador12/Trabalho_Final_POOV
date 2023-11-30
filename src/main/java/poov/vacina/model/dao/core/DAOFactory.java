package poov.vacina.model.dao.core;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;

public class DAOFactory {

    private ConnectionFactory conexaoFactory;
    private Connection conexao = null;

    public DAOFactory(ConnectionFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    public <T extends DAO<?, ?>> T getDAO(Class<T> daoClazz) {
        if (conexao == null) {
            throw new IllegalStateException("Abra uma conexão antes de criar um DAO.");
        } else {
            try {
                return daoClazz.getDeclaredConstructor(Connection.class).newInstance(conexao);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new InvalidParameterException(
                        "Não foi possível criar um DAO do tipo " + daoClazz.getName() + ".");
            }
        }
    }

    public void abrirConexao() throws SQLException {
        if (conexao == null) {
            conexao = conexaoFactory.getConnection();
        } else {
            throw new IllegalStateException("A conexão já está aberta.");
        }
    }

    public void fecharConexao() {
        if (conexao != null) {
            System.out.println("Terminando a conexão com o banco de dados.");
            try {
                conexao.close();
                conexao = null;
                System.out.println("Conexão com o banco de dados terminada.");
            } catch (SQLException ex) {
                System.out.println("Erro fechando a conexão com o banco de dados.");
            }
        } else {
            throw new IllegalStateException("A conexão com o BD já está fechada.");
        }
    }

    public void iniciarTransacao() throws SQLException {
        conexao.setAutoCommit(false);
    }

    public void terminarTransacao() throws SQLException {
        try {
            conexao.commit();
        } finally {
            conexao.setAutoCommit(true);
        }
    }

    public void abortarTransacao() throws SQLException {
        try {
            conexao.rollback();
        } finally {
            conexao.setAutoCommit(true);
        }
    }
}