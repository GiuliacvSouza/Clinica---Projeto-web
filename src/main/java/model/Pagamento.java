package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id
    @Column(name = "idpagamento", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfatura")
    private Fatura idfatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilizador")
    private Utilizador idutilizador;

    @Column(name = "datapagamento")
    private LocalDate datapagamento;

    @Column(name = "valorpago", precision = 10, scale = 2)
    private BigDecimal valorpago;

    @Column(name = "metodo", length = 50)
    private String metodo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Fatura getIdfatura() {
        return idfatura;
    }

    public void setIdfatura(Fatura idfatura) {
        this.idfatura = idfatura;
    }

    public Utilizador getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Utilizador idutilizador) {
        this.idutilizador = idutilizador;
    }

    public LocalDate getDatapagamento() {
        return datapagamento;
    }

    public void setDatapagamento(LocalDate datapagamento) {
        this.datapagamento = datapagamento;
    }

    public BigDecimal getValorpago() {
        return valorpago;
    }

    public void setValorpago(BigDecimal valorpago) {
        this.valorpago = valorpago;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

}