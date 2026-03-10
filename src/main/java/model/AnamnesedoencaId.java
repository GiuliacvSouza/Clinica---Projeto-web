package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnamnesedoencaId implements Serializable {
    private static final long serialVersionUID = 8077075600272446396L;
    @Column(name = "idanamnese", nullable = false)
    private Integer idanamnese;

    @Column(name = "iddoenca", nullable = false)
    private Integer iddoenca;

    public Integer getIdanamnese() {
        return idanamnese;
    }

    public void setIdanamnese(Integer idanamnese) {
        this.idanamnese = idanamnese;
    }

    public Integer getIddoenca() {
        return iddoenca;
    }

    public void setIddoenca(Integer iddoenca) {
        this.iddoenca = iddoenca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnamnesedoencaId entity = (AnamnesedoencaId) o;
        return Objects.equals(this.idanamnese, entity.idanamnese) &&
                Objects.equals(this.iddoenca, entity.iddoenca);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idanamnese, iddoenca);
    }
}