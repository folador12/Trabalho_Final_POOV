package poov.vacina.model;

public enum Situacao {

    ATIVO("Ativo"), INATIVO("Inativo");

    private Situacao(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

}
