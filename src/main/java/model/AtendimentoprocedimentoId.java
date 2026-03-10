package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AtendimentoprocedimentoId implements Serializable {
    private static final long serialVersionUID = 8035184220406930580L;
    @Column(name = "idprocedimento", nullable = false)
    private Integer idprocedimento;

    @Column(name = "idatendimento", nullable = false)
    private Integer idatendimento;

    public Integer getIdprocedimento() {
        return idprocedimento;
    }

    public void setIdprocedimento(Integer idprocedimento) {
        this.idprocedimento = idprocedimento;
    }

    public Integer getIdatendimento() {
        return idatendimento;
    }

    public void setIdatendimento(Integer idatendimento) {
        this.idatendimento = idatendimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtendimentoprocedimentoId entity = (AtendimentoprocedimentoId) o;
        return Objects.equals(this.idprocedimento, entity.idprocedimento) &&
                Objects.equals(this.idatendimento, entity.idatendimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idprocedimento, idatendimento);
    }
}