package model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "atendimentoprocedimento")
public class Atendimentoprocedimento {
    @EmbeddedId
    private AtendimentoprocedimentoId id;

    @MapsId("idprocedimento")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idprocedimento", nullable = false)
    private Procedimento idprocedimento;

    @MapsId("idatendimento")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idatendimento", nullable = false)
    private Atendimento idatendimento;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "desconto", precision = 10, scale = 2)
    private BigDecimal desconto;

    public AtendimentoprocedimentoId getId() {
        return id;
    }

    public void setId(AtendimentoprocedimentoId id) {
        this.id = id;
    }

    public Procedimento getIdprocedimento() {
        return idprocedimento;
    }

    public void setIdprocedimento(Procedimento idprocedimento) {
        this.idprocedimento = idprocedimento;
    }

    public Atendimento getIdatendimento() {
        return idatendimento;
    }

    public void setIdatendimento(Atendimento idatendimento) {
        this.idatendimento = idatendimento;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

}