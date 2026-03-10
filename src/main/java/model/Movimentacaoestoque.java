package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "movimentacaoestoque")
public class Movimentacaoestoque {
    @Id
    @Column(name = "idmovimentacao", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idmaterial")
    private Material idmaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilizador")
    private Assistente idutilizador;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "motivo", length = Integer.MAX_VALUE)
    private String motivo;

    @Column(name = "observacao", length = Integer.MAX_VALUE)
    private String observacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Material getIdmaterial() {
        return idmaterial;
    }

    public void setIdmaterial(Material idmaterial) {
        this.idmaterial = idmaterial;
    }

    public Assistente getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Assistente idutilizador) {
        this.idutilizador = idutilizador;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}