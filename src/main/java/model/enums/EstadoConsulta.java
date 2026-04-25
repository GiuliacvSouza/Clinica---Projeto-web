package model.enums;

public enum EstadoConsulta {
    AGENDADA("Agendada"),
    CONFIRMADA("Confirmada"),
    EM_ATENDIMENTO("Em Atendimento"),
    CONCLUIDA("Concluída"),
    FATURADA("Faturada"),
    CANCELADA("Cancelada"),
    FALTA("Falta"),
    PENDENTE("Pendente"),
    EM_ESPERA("Em Espera"),
    EM_CONSULTA("Em Consulta");

    private final String descricao;

    EstadoConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
