package model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fatura")
public class Fatura {
    @Id
    @Column(name = "idfatura", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idatendimento")
    private Atendimento idatendimento;

    @Column(name = "dataemissao")
    private LocalDate dataemissao;

    @Column(name = "valorfinal", precision = 10, scale = 2)
    private BigDecimal valorfinal;

    @Column(name = "estado", length = 30)
    private String estado;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Atendimento getIdatendimento() {
        return idatendimento;
    }

    public void setIdatendimento(Atendimento idatendimento) {
        this.idatendimento = idatendimento;
    }

    public LocalDate getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(LocalDate dataemissao) {
        this.dataemissao = dataemissao;
    }

    public BigDecimal getValorfinal() {
        return valorfinal;
    }

    public void setValorfinal(BigDecimal valorfinal) {
        this.valorfinal = valorfinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}