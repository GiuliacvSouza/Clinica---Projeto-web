package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "recepcionista")
public class Recepcionista {
    @Id
    @Column(name = "idutilizador", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Utilizador utilizador;

    @Column(name = "dataadmissao")
    private LocalDate dataadmissao;

    @Column(name = "turno", length = 50)
    private String turno;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilizador getUtilizador() {
        return utilizador;
    }

    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador;
    }

    public LocalDate getDataadmissao() {
        return dataadmissao;
    }

    public void setDataadmissao(LocalDate dataadmissao) {
        this.dataadmissao = dataadmissao;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

}