package model;

import jakarta.persistence.*;

@Entity
@Table(name = "pedidocompra")
public class Pedidocompra {
    @Id
    @Column(name = "idpedido", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfornecedor")
    private Fornecedor idfornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idassistente")
    private Assistente idassistente;

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

    public Assistente getIdassistente() {
        return idassistente;
    }

    public void setIdassistente(Assistente idassistente) {
        this.idassistente = idassistente;
    }

}