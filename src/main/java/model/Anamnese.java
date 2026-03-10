package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "anamnese")
public class Anamnese {
    @Id
    @Column(name = "idanamnese", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idatendimento")
    private Atendimento idatendimento;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "motivo", length = Integer.MAX_VALUE)
    private String motivo;

    @Column(name = "queixaprincipal", length = Integer.MAX_VALUE)
    private String queixaprincipal;

    @Column(name = "diabetes")
    private Boolean diabetes;

    @Column(name = "hipertensao")
    private Boolean hipertensao;

    @Column(name = "doencagrave")
    private Boolean doencagrave;

    @Column(name = "hepatite")
    private Boolean hepatite;

    @Column(name = "outrasdoencas", length = Integer.MAX_VALUE)
    private String outrasdoencas;

    @Column(name = "usamedicamento")
    private Boolean usamedicamento;

    @Column(name = "temalergia")
    private Boolean temalergia;

    @Column(name = "efumante")
    private Boolean efumante;

    @Column(name = "observacoes", length = Integer.MAX_VALUE)
    private String observacoes;

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

    public String getQueixaprincipal() {
        return queixaprincipal;
    }

    public void setQueixaprincipal(String queixaprincipal) {
        this.queixaprincipal = queixaprincipal;
    }

    public Boolean getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Boolean diabetes) {
        this.diabetes = diabetes;
    }

    public Boolean getHipertensao() {
        return hipertensao;
    }

    public void setHipertensao(Boolean hipertensao) {
        this.hipertensao = hipertensao;
    }

    public Boolean getDoencagrave() {
        return doencagrave;
    }

    public void setDoencagrave(Boolean doencagrave) {
        this.doencagrave = doencagrave;
    }

    public Boolean getHepatite() {
        return hepatite;
    }

    public void setHepatite(Boolean hepatite) {
        this.hepatite = hepatite;
    }

    public String getOutrasdoencas() {
        return outrasdoencas;
    }

    public void setOutrasdoencas(String outrasdoencas) {
        this.outrasdoencas = outrasdoencas;
    }

    public Boolean getUsamedicamento() {
        return usamedicamento;
    }

    public void setUsamedicamento(Boolean usamedicamento) {
        this.usamedicamento = usamedicamento;
    }

    public Boolean getTemalergia() {
        return temalergia;
    }

    public void setTemalergia(Boolean temalergia) {
        this.temalergia = temalergia;
    }

    public Boolean getEfumante() {
        return efumante;
    }

    public void setEfumante(Boolean efumante) {
        this.efumante = efumante;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

}