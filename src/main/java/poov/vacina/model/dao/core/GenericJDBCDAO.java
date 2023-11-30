package poov.vacina.model.dao.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericJDBCDAO<T, PK> implements DAO<T, PK> {

    protected final Connection connection;

    public GenericJDBCDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public T findById(PK key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        try {
            PreparedStatement statement = connection.prepareStatement(findByKeyQuery());
            setKeyInStatement(statement, key);
            // statement.setLong(1, key);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return toEntity(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        try {
            PreparedStatement statement = connection.prepareStatement(findAllQuery());
            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }
        return new ArrayList<T>();
    }

    @Override
    public <S extends T> S update(S entity) {
        try {
            PreparedStatement statement = connection.prepareStatement(updateQuery());
            addParameters(statement, entity);
            int rows = statement.executeUpdate();
            if (rows > 0) {
                return entity;
            } else {
                throw new SQLException("No row updated");
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return null;
    }

    @Override
    public <S extends T> S create(S entity) {
        try {
            PreparedStatement statement = connection.prepareStatement(createQuery(), Statement.RETURN_GENERATED_KEYS);
            addParameters(statement, entity);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                setKeyInEntity(keys, entity);
                // entity.setKey(keys.getLong(1));
            }
            return entity;
        } catch (SQLException e) {
            showSQLException(e);
        }
        return null;
    }

    @Override
    public void deleteById(PK key) {
        try {
            PreparedStatement statement = connection.prepareStatement(removeQuery());
            setKeyInStatement(statement, key);
            statement.executeUpdate();
        } catch (SQLException e) {
            showSQLException(e);
        }
    }

    @Override
    public void delete(T entity) {
        try {
            PreparedStatement statement = connection.prepareStatement(removeQuery());
            setKeyInStatementFromEntity(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            showSQLException(e);
        }
    }

    protected List<T> toEntityList(ResultSet resultSet) throws SQLException {
        List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(toEntity(resultSet));
        }
        return result;
    }

    protected abstract void setKeyInStatementFromEntity(PreparedStatement statement, T entity) throws SQLException;

    protected abstract void setKeyInStatement(PreparedStatement statement, PK key) throws SQLException;

    protected abstract void setKeyInEntity(ResultSet rs, T entity) throws SQLException;

    protected abstract T toEntity(ResultSet resultSet) throws SQLException;

    protected abstract void addParameters(PreparedStatement resultSet, T entity) throws SQLException;

    protected abstract String findByKeyQuery();

    protected abstract String findAllQuery();

    protected abstract String updateQuery();

    protected abstract String createQuery();

    protected abstract String removeQuery();

    public static void showSQLException(SQLException ex) {
        System.out.println("Erro no acesso ao banco de dados.");
        SQLException e = ex;
        while (e != null) {
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Mensagem: " + e.getMessage());
            Throwable t = e.getCause();
            while (t != null) {
                System.out.println("Causa: " + t);
                t = t.getCause();
            }
            e = e.getNextException();
        }
    }

}
