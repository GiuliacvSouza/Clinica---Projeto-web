package model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "especialidadexassistente")
public class Especialidadexassistente {
    @EmbeddedId
    private EspecialidadexassistenteId id;

    @MapsId("idutilizador")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Assistente idutilizador;

    @MapsId("idespecialidade")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idespecialidade", nullable = false)
    private Especialidade idespecialidade;

    public EspecialidadexassistenteId getId() {
        return id;
    }

    public void setId(EspecialidadexassistenteId id) {
        this.id = id;
    }

    public Assistente getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Assistente idutilizador) {
        this.idutilizador = idutilizador;
    }

    public Especialidade getIdespecialidade() {
        return idespecialidade;
    }

    public void setIdespecialidade(Especialidade idespecialidade) {
        this.idespecialidade = idespecialidade;
    }

}