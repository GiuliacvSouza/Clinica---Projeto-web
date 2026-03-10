package model;

import jakarta.persistence.*;

@Entity
@Table(name = "atendimento")
public class Atendimento {
    @Id
    @Column(name = "idatendimento", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idconsulta")
    private Consulta idconsulta;

    @Column(name = "diagnostico", length = Integer.MAX_VALUE)
    private String diagnostico;

    @Column(name = "retorno")
    private Boolean retorno;

    @Column(name = "periodoretorno")
    private Integer periodoretorno;

    @Column(name = "observacoes", length = Integer.MAX_VALUE)
    private String observacoes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Consulta getIdconsulta() {
        return idconsulta;
    }

    public void setIdconsulta(Consulta idconsulta) {
        this.idconsulta = idconsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public Boolean getRetorno() {
        return retorno;
    }

    public void setRetorno(Boolean retorno) {
        this.retorno = retorno;
    }

    public Integer getPeriodoretorno() {
        return periodoretorno;
    }

    public void setPeriodoretorno(Integer periodoretorno) {
        this.periodoretorno = periodoretorno;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}