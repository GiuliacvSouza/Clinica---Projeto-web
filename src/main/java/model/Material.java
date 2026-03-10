package model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "material")
public class Material {
    @Id
    @Column(name = "idmaterial", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfornecedor")
    private Fornecedor idfornecedor;

    @Column(name = "nome", length = 100)
    private String nome;

    @Column(name = "descricao", length = Integer.MAX_VALUE)
    private String descricao;

    @Column(name = "unidademedida", length = 20)
    private String unidademedida;

    @Column(name = "quantidadeatual")
    private Integer quantidadeatual;

    @Column(name = "quantidademinima")
    private Integer quantidademinima;

    @Column(name = "valorunitario", precision = 10, scale = 2)
    private BigDecimal valorunitario;

    @Column(name = "ativo")
    private Boolean ativo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Fornecedor getIdfornecedor() {
        return idfornecedor;
    }

    public void setIdfornecedor(Fornecedor idfornecedor) {
        this.idfornecedor = idfornecedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidademedida() {
        return unidademedida;
    }

    public void setUnidademedida(String unidademedida) {
        this.unidademedida = unidademedida;
    }

    public Integer getQuantidadeatual() {
        return quantidadeatual;
    }

    public void setQuantidadeatual(Integer quantidadeatual) {
        this.quantidadeatual = quantidadeatual;
    }

    public Integer getQuantidademinima() {
        return quantidademinima;
    }

    public void setQuantidademinima(Integer quantidademinima) {
        this.quantidademinima = quantidademinima;
    }

    public BigDecimal getValorunitario() {
        return valorunitario;
    }

    public void setValorunitario(BigDecimal valorunitario) {
        this.valorunitario = valorunitario;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}