package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "assistente")
public class Assistente {
    @Id
    @Column(name = "idutilizador", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Utilizador utilizador;

    @Column(name = "nivelformacao", length = 100)
    private String nivelformacao;

    @Column(name = "dataadmissao")
    private LocalDate dataadmissao;

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

    public String getNivelformacao() {
        return nivelformacao;
    }

    public void setNivelformacao(String nivelformacao) {
        this.nivelformacao = nivelformacao;
    }

    public LocalDate getDataadmissao() {
        return dataadmissao;
    }

    public void setDataadmissao(LocalDate dataadmissao) {
        this.dataadmissao = dataadmissao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}