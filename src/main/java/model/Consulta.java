package model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "consulta")
public class Consulta {
    @Id
    @Column(name = "idconsulta", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpaciente")
    private Paciente idpaciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddentista")
    private Dentista iddentista;

    @Column(name = "datahorainicio")
    private Instant datahorainicio;

    @Column(name = "duracao")
    private Integer duracao;

    @Column(name = "tipo", length = 50)
    private String tipo;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "observacoes", length = Integer.MAX_VALUE)
    private String observacoes;

    @Column(name = "datamarcacao")
    private LocalDate datamarcacao;

    @Column(name = "motivocancelamento", length = Integer.MAX_VALUE)
    private String motivocancelamento;

    @Column(name = "datacancelamento")
    private LocalDate datacancelamento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Paciente getIdpaciente() {
        return idpaciente;
    }

    public void setIdpaciente(Paciente idpaciente) {
        this.idpaciente = idpaciente;
    }

    public Dentista getIddentista() {
        return iddentista;
    }

    public void setIddentista(Dentista iddentista) {
        this.iddentista = iddentista;
    }

    public Instant getDatahorainicio() {
        return datahorainicio;
    }

    public void setDatahorainicio(Instant datahorainicio) {
        this.datahorainicio = datahorainicio;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDate getDatamarcacao() {
        return datamarcacao;
    }

    public void setDatamarcacao(LocalDate datamarcacao) {
        this.datamarcacao = datamarcacao;
    }

    public String getMotivocancelamento() {
        return motivocancelamento;
    }

    public void setMotivocancelamento(String motivocancelamento) {
        this.motivocancelamento = motivocancelamento;
    }

    public LocalDate getDatacancelamento() {
        return datacancelamento;
    }

    public void setDatacancelamento(LocalDate datacancelamento) {
        this.datacancelamento = datacancelamento;
    }

}