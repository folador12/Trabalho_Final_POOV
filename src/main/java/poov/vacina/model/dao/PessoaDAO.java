package poov.vacina.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import poov.vacina.model.Situacao;
import poov.vacina.model.Pessoa;
import poov.vacina.model.dao.core.GenericJDBCDAO;

public class PessoaDAO extends GenericJDBCDAO<Pessoa, Long> {

    public PessoaDAO(Connection conexao) {
        super(conexao);
    }

    private static final String FIND_ALL_QUERY = "SELECT codigo, nome, cpf, dataNascimento, situacao FROM pessoa WHERE situacao = 'ATIVO' ";
    private static final String FIND_BY_KEY_QUERY = FIND_ALL_QUERY + "AND codigo=? ";
    private static final String UPDATE_QUERY = "UPDATE pessoa SET nome=?, cpf=?, dataNascimento=?, situacao=? WHERE codigo=?";
    private static final String CREATE_QUERY = "INSERT INTO pessoa (nome, cpf, dataNascimento) VALUES (?, ?, ?)";
    private static final String REMOVE_QUERY = "DELETE FROM pessoa WHERE codigo=?";

    @Override
    protected Pessoa toEntity(ResultSet resultSet) throws SQLException {
        Pessoa Pessoa = new Pessoa();
        Pessoa.setCodigo(resultSet.getLong("codigo"));
        Pessoa.setNome(resultSet.getString("nome"));
        Pessoa.setCpf(resultSet.getString("cpf"));
        Pessoa.setDataNascimento(resultSet.getDate("dataNascimento").toLocalDate());
        if (resultSet.getString("situacao").equals("ATIVO")) {
            Pessoa.setSituacao(Situacao.ATIVO);
        } else {
            Pessoa.setSituacao(Situacao.INATIVO);
        }
        return Pessoa;
    }

    @Override
    protected void addParameters(PreparedStatement pstmt, Pessoa entity) throws SQLException {
        pstmt.setString(1, entity.getNome());
        pstmt.setString(2, entity.getCpf());
        pstmt.setDate(3, java.sql.Date.valueOf(entity.getDataNascimento()));
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
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Pessoa entity) throws SQLException {
        statement.setLong(1, entity.getCodigo());
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        statement.setLong(1, key);
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Pessoa entity) throws SQLException {
        entity.setCodigo(rs.getLong(1));
    }
}
