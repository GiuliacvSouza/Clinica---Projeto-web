package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnamnesealergiaId implements Serializable {
    private static final long serialVersionUID = -6904347169111923944L;
    @Column(name = "idanamnese", nullable = false)
    private Integer idanamnese;

    @Column(name = "idalergia", nullable = false)
    private Integer idalergia;

    public Integer getIdanamnese() {
        return idanamnese;
    }

    public void setIdanamnese(Integer idanamnese) {
        this.idanamnese = idanamnese;
    }

    public Integer getIdalergia() {
        return idalergia;
    }

    public void setIdalergia(Integer idalergia) {
        this.idalergia = idalergia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnamnesealergiaId entity = (AnamnesealergiaId) o;
        return Objects.equals(this.idanamnese, entity.idanamnese) &&
                Objects.equals(this.idalergia, entity.idalergia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idanamnese, idalergia);
    }
}