package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EspecialidadedentistaId implements Serializable {
    private static final long serialVersionUID = 7716173453720548128L;
    @Column(name = "idutilizador", nullable = false)
    private Integer idutilizador;

    @Column(name = "idespecialidade", nullable = false)
    private Integer idespecialidade;

    public Integer getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Integer idutilizador) {
        this.idutilizador = idutilizador;
    }

    public Integer getIdespecialidade() {
        return idespecialidade;
    }

    public void setIdespecialidade(Integer idespecialidade) {
        this.idespecialidade = idespecialidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EspecialidadedentistaId entity = (EspecialidadedentistaId) o;
        return Objects.equals(this.idutilizador, entity.idutilizador) &&
                Objects.equals(this.idespecialidade, entity.idespecialidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idutilizador, idespecialidade);
    }
}