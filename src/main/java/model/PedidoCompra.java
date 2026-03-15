package model;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "pedidoCompra")
public class PedidoCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPedido", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idFornecedor")
    private Fornecedor idFornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAssistente")
    private Assistente idAssistente;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Fornecedor getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Fornecedor idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Assistente getIdAssistente() {
        return idAssistente;
    }

    public void setIdAssistente(Assistente idAssistente) {
        this.idAssistente = idAssistente;
    }
}