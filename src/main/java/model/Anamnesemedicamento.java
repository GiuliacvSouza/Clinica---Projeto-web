package model;

import jakarta.persistence.*;

@Entity
@Table(name = "anamnesemedicamento")
public class Anamnesemedicamento {
    @EmbeddedId
    private AnamnesemedicamentoId id;

    @MapsId("idanamnese")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idanamnese", nullable = false)
    private Anamnese idanamnese;

    @MapsId("idmedicamento")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idmedicamento", nullable = false)
    private Medicamento idmedicamento;

    @Column(name = "dosagem", length = 100)
    private String dosagem;

    public AnamnesemedicamentoId getId() {
        return id;
    }

    public void setId(AnamnesemedicamentoId id) {
        this.id = id;
    }

    public Anamnese getIdanamnese() {
        return idanamnese;
    }

    public void setIdanamnese(Anamnese idanamnese) {
        this.idanamnese = idanamnese;
    }

    public Medicamento getIdmedicamento() {
        return idmedicamento;
    }

    public void setIdmedicamento(Medicamento idmedicamento) {
        this.idmedicamento = idmedicamento;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

}