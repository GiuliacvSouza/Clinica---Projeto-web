package model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "anamnesealergia")
public class Anamnesealergia {
    @EmbeddedId
    private AnamnesealergiaId id;

    @MapsId("idanamnese")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idanamnese", nullable = false)
    private Anamnese idanamnese;

    @MapsId("idalergia")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idalergia", nullable = false)
    private Alergia idalergia;

    @Column(name = "gravidade", length = 50)
    private String gravidade;

    public AnamnesealergiaId getId() {
        return id;
    }

    public void setId(AnamnesealergiaId id) {
        this.id = id;
    }

    public Anamnese getIdanamnese() {
        return idanamnese;
    }

    public void setIdanamnese(Anamnese idanamnese) {
        this.idanamnese = idanamnese;
    }

    public Alergia getIdalergia() {
        return idalergia;
    }

    public void setIdalergia(Alergia idalergia) {
        this.idalergia = idalergia;
    }

    public String getGravidade() {
        return gravidade;
    }

    public void setGravidade(String gravidade) {
        this.gravidade = gravidade;
    }

}