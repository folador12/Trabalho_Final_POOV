package poov.vacina.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import poov.vacina.model.Situacao;
import poov.vacina.model.Aplicacao;
import poov.vacina.model.dao.core.GenericJDBCDAO;

public class AplicacaoDAO extends GenericJDBCDAO<Aplicacao, Long> {

    public AplicacaoDAO(Connection conexao) {
        super(conexao);
    }

    private static final String FIND_ALL_QUERY = "SELECT codigo, data, pessoa_id, vacina_id, situacao FROM aplicacao";
    private static final String FIND_BY_KEY_QUERY = FIND_ALL_QUERY + " WHERE codigo=?";
    private static final String CREATE_QUERY = "INSERT INTO aplicacao (data, pessoa_id, vacina_id) VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE aplicacao SET data=?, pessoa_id=?, vacina_id=?, situacao=? WHERE codigo=?";
    private static final String REMOVE_QUERY = "DELETE FROM aplicacao WHERE codigo=?";

    @Override
    protected Aplicacao toEntity(ResultSet resultSet) throws SQLException {
        Aplicacao Aplicacao = new Aplicacao();
        Aplicacao.setCodigo(resultSet.getLong("codigo"));
        Aplicacao.setData(resultSet.getDate("dataNascimento").toLocalDate());

        Aplicacao.getPessoa().setCodigo(resultSet.getLong("codigo"));
        Aplicacao.getPessoa().setNome(resultSet.getString("nome"));

        if (resultSet.getString("situacao").equals("ATIVO")) {
            Aplicacao.setSituacao(Situacao.ATIVO);
        } else {
            Aplicacao.setSituacao(Situacao.INATIVO);
        }
        return Aplicacao;
    }

    @Override
    protected void addParameters(PreparedStatement pstmt, Aplicacao entity) throws SQLException {
        pstmt.setDate(1, java.sql.Date.valueOf(entity.getData()));
        pstmt.setLong(2, entity.getPessoa().getCodigo());
        pstmt.setLong(3, entity.getVacina().getCodigo());

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

    @Override
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Aplicacao entity) throws SQLException {
        statement.setLong(1, entity.getCodigo());
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        statement.setLong(1, key);
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Aplicacao entity) throws SQLException {
        entity.setCodigo(rs.getLong(1));
    }
}
