package poov.vacina.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import poov.vacina.model.Situacao;
import poov.vacina.model.Vacina;
import poov.vacina.model.dao.core.GenericJDBCDAO;

public class VacinaDAO extends GenericJDBCDAO<Vacina, Long> {

    public VacinaDAO(Connection conexao) {
        super(conexao);
    }

    private static final String FIND_ALL_QUERY = "SELECT codigo, nome, descricao, situacao FROM vacina WHERE situacao='ATIVO' ";
    private static final String FIND_BY_KEY_QUERY = FIND_ALL_QUERY + "AND codigo=? ";
    private static final String FIND_BY_NAME_LIKE_QUERY = FIND_ALL_QUERY + "AND upper(nome) like upper(?)";
    private static final String UPDATE_QUERY = "UPDATE vacina SET nome=?, descricao=?, situacao=? WHERE codigo=?";
    private static final String CREATE_QUERY = "INSERT INTO vacina (nome, descricao, situacao) VALUES (?, ?, ?)";
    private static final String REMOVE_QUERY = "DELETE FROM vacina WHERE codigo=?";

    @Override
    protected Vacina toEntity(ResultSet resultSet) throws SQLException {
        Vacina vacina = new Vacina();
        vacina.setCodigo(resultSet.getLong("codigo"));
        vacina.setNome(resultSet.getString("nome"));
        vacina.setDescricao(resultSet.getString("descricao"));
        if (resultSet.getString("situacao").equals("ATIVO")) {
            vacina.setSituacao(Situacao.ATIVO);
        } else {
            vacina.setSituacao(Situacao.INATIVO);
        }
        return vacina;
    }

    @Override
    protected void addParameters(PreparedStatement pstmt, Vacina entity) throws SQLException {
        pstmt.setString(1, entity.getNome());
        pstmt.setString(2, entity.getDescricao());
        pstmt.setString(3, entity.getSituacao().toString());
        if (entity.getCodigo() != null) {
            pstmt.setLong(4, entity.getCodigo());
        }
    }

    @Override
    protected String findByKeyQuery() {
        return FIND_BY_KEY_QUERY;
    }

    @Override
    protected String findAllQuery() {
        return FIND_ALL_QUERY;
    }

    @Override
    protected String updateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected String createQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected String removeQuery() {
        return REMOVE_QUERY;
    }

    public List<Vacina> findByNameLike(String nome) {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_LIKE_QUERY);
            statement.setString(1, "%" + nome + "%");
            ResultSet resultSet = statement.executeQuery();
            return toEntityList(resultSet);
        } catch (SQLException e) {
            showSQLException(e);
        }
        return new ArrayList<Vacina>();
    }

    @Override
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Vacina entity) throws SQLException {
        statement.setLong(1, entity.getCodigo());
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        statement.setLong(1, key);
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Vacina entity) throws SQLException {
        entity.setCodigo(rs.getLong(1));
    }

}
