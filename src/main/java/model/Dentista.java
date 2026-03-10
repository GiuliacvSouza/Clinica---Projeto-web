package model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "dentista")
public class Dentista {
    @Id
    @Column(name = "idutilizador", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Utilizador utilizador;

    @Column(name = "numeroomd", length = 50)
    private String numeroomd;

    @Column(name = "dataadmissao")
    private LocalDate dataadmissao;

    @Column(name = "horarioentrada")
    private LocalTime horarioentrada;

    @Column(name = "horariosaida")
    private LocalTime horariosaida;

    @Column(name = "ativo")
    private Boolean ativo;

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

    public String getNumeroomd() {
        return numeroomd;
    }

    public void setNumeroomd(String numeroomd) {
        this.numeroomd = numeroomd;
    }

    public LocalDate getDataadmissao() {
        return dataadmissao;
    }

    public void setDataadmissao(LocalDate dataadmissao) {
        this.dataadmissao = dataadmissao;
    }

    public LocalTime getHorarioentrada() {
        return horarioentrada;
    }

    public void setHorarioentrada(LocalTime horarioentrada) {
        this.horarioentrada = horarioentrada;
    }

    public LocalTime getHorariosaida() {
        return horariosaida;
    }

    public void setHorariosaida(LocalTime horariosaida) {
        this.horariosaida = horariosaida;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}