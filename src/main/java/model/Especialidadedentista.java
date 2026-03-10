package model;

import jakarta.persistence.*;

@Entity
@Table(name = "especialidadedentista")
public class Especialidadedentista {
    @EmbeddedId
    private EspecialidadedentistaId id;

    @MapsId("idutilizador")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Dentista idutilizador;

    @MapsId("idespecialidade")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idespecialidade", nullable = false)
    private Especialidade idespecialidade;

    public EspecialidadedentistaId getId() {
        return id;
    }

    public void setId(EspecialidadedentistaId id) {
        this.id = id;
    }

    public Dentista getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Dentista idutilizador) {
        this.idutilizador = idutilizador;
    }

    public Especialidade getIdespecialidade() {
        return idespecialidade;
    }

    public void setIdespecialidade(Especialidade idespecialidade) {
        this.idespecialidade = idespecialidade;
    }

}