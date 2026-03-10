package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnamnesemedicamentoId implements Serializable {
    private static final long serialVersionUID = -3797682857376391926L;
    @Column(name = "idanamnese", nullable = false)
    private Integer idanamnese;

    @Column(name = "idmedicamento", nullable = false)
    private Integer idmedicamento;

    public Integer getIdanamnese() {
        return idanamnese;
    }

    public void setIdanamnese(Integer idanamnese) {
        this.idanamnese = idanamnese;
    }

    public Integer getIdmedicamento() {
        return idmedicamento;
    }

    public void setIdmedicamento(Integer idmedicamento) {
        this.idmedicamento = idmedicamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnamnesemedicamentoId entity = (AnamnesemedicamentoId) o;
        return Objects.equals(this.idanamnese, entity.idanamnese) &&
                Objects.equals(this.idmedicamento, entity.idmedicamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idanamnese, idmedicamento);
    }
}