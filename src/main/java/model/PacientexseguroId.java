package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PacientexseguroId implements Serializable {
    private static final long serialVersionUID = 5454738706395719972L;
    @Column(name = "idutilizador", nullable = false)
    private Integer idutilizador;

    @Column(name = "idseguro", nullable = false)
    private Integer idseguro;

    public Integer getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Integer idutilizador) {
        this.idutilizador = idutilizador;
    }

    public Integer getIdseguro() {
        return idseguro;
    }

    public void setIdseguro(Integer idseguro) {
        this.idseguro = idseguro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacientexseguroId entity = (PacientexseguroId) o;
        return Objects.equals(this.idutilizador, entity.idutilizador) &&
                Objects.equals(this.idseguro, entity.idseguro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idutilizador, idseguro);
    }
}