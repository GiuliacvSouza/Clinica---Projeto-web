package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "prontuario")
public class Prontuario {
    @Id
    @Column(name = "idutilizador", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Paciente paciente;

    @Column(name = "datacriacao")
    private LocalDate datacriacao;

    @Column(name = "ultimaatualizacao")
    private LocalDate ultimaatualizacao;

    @Column(name = "gruposanguineo", length = 5)
    private String gruposanguineo;

    @Column(name = "observacoes", length = Integer.MAX_VALUE)
    private String observacoes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDate getDatacriacao() {
        return datacriacao;
    }

    public void setDatacriacao(LocalDate datacriacao) {
        this.datacriacao = datacriacao;
    }

    public LocalDate getUltimaatualizacao() {
        return ultimaatualizacao;
    }

    public void setUltimaatualizacao(LocalDate ultimaatualizacao) {
        this.ultimaatualizacao = ultimaatualizacao;
    }

    public String getGruposanguineo() {
        return gruposanguineo;
    }

    public void setGruposanguineo(String gruposanguineo) {
        this.gruposanguineo = gruposanguineo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}