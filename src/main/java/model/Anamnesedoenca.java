package model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "anamnesedoenca")
public class Anamnesedoenca {
    @EmbeddedId
    private AnamnesedoencaId id;

    @MapsId("idanamnese")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idanamnese", nullable = false)
    private Anamnese idanamnese;

    @MapsId("iddoenca")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "iddoenca", nullable = false)
    private Doenca iddoenca;

    @Column(name = "descricaopaciente", length = Integer.MAX_VALUE)
    private String descricaopaciente;

    public AnamnesedoencaId getId() {
        return id;
    }

    public void setId(AnamnesedoencaId id) {
        this.id = id;
    }

    public Anamnese getIdanamnese() {
        return idanamnese;
    }

    public void setIdanamnese(Anamnese idanamnese) {
        this.idanamnese = idanamnese;
    }

    public Doenca getIddoenca() {
        return iddoenca;
    }

    public void setIddoenca(Doenca iddoenca) {
        this.iddoenca = iddoenca;
    }

    public String getDescricaopaciente() {
        return descricaopaciente;
    }

    public void setDescricaopaciente(String descricaopaciente) {
        this.descricaopaciente = descricaopaciente;
    }

}