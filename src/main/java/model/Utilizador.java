package model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "utilizador")
public class Utilizador {
    @Id
    @Column(name = "idutilizador", nullable = false)
    private Integer id;

    @Column(name = "primeironome", length = 100)
    private String primeironome;

    @Column(name = "ultimonome", length = 100)
    private String ultimonome;

    @Column(name = "tipoutilizador", length = 50)
    private String tipoutilizador;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "nif", length = 20)
    private String nif;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "telemovel", length = 20)
    private String telemovel;

    @Column(name = "datanascimento")
    private LocalDate datanascimento;

    @Column(name = "ultimoacesso")
    private Instant ultimoacesso;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "rua", length = 150)
    private String rua;

    @Column(name = "numeroporta", length = 10)
    private String numeroporta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigopostal")
    private Codigopostal codigopostal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrimeironome() {
        return primeironome;
    }

    public void setPrimeironome(String primeironome) {
        this.primeironome = primeironome;
    }

    public String getUltimonome() {
        return ultimonome;
    }

    public void setUltimonome(String ultimonome) {
        this.ultimonome = ultimonome;
    }

    public String getTipoutilizador() {
        return tipoutilizador;
    }

    public void setTipoutilizador(String tipoutilizador) {
        this.tipoutilizador = tipoutilizador;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelemovel() {
        return telemovel;
    }

    public void setTelemovel(String telemovel) {
        this.telemovel = telemovel;
    }

    public LocalDate getDatanascimento() {
        return datanascimento;
    }

    public void setDatanascimento(LocalDate datanascimento) {
        this.datanascimento = datanascimento;
    }

    public Instant getUltimoacesso() {
        return ultimoacesso;
    }

    public void setUltimoacesso(Instant ultimoacesso) {
        this.ultimoacesso = ultimoacesso;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumeroporta() {
        return numeroporta;
    }

    public void setNumeroporta(String numeroporta) {
        this.numeroporta = numeroporta;
    }

    public Codigopostal getCodigopostal() {
        return codigopostal;
    }

    public void setCodigopostal(Codigopostal codigopostal) {
        this.codigopostal = codigopostal;
    }

}