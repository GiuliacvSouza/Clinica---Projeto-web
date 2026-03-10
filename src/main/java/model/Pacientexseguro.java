package model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "pacientexseguro")
public class Pacientexseguro {
    @EmbeddedId
    private PacientexseguroId id;

    @MapsId("idutilizador")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idutilizador", nullable = false)
    private Paciente idutilizador;

    @MapsId("idseguro")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idseguro", nullable = false)
    private Seguro idseguro;

    @Column(name = "numeroapolice", length = 50)
    private String numeroapolice;

    @Column(name = "datainiciocobertura")
    private LocalDate datainiciocobertura;

    @Column(name = "datafimcobertura")
    private LocalDate datafimcobertura;

    public PacientexseguroId getId() {
        return id;
    }

    public void setId(PacientexseguroId id) {
        this.id = id;
    }

    public Paciente getIdutilizador() {
        return idutilizador;
    }

    public void setIdutilizador(Paciente idutilizador) {
        this.idutilizador = idutilizador;
    }

    public Seguro getIdseguro() {
        return idseguro;
    }

    public void setIdseguro(Seguro idseguro) {
        this.idseguro = idseguro;
    }

    public String getNumeroapolice() {
        return numeroapolice;
    }

    public void setNumeroapolice(String numeroapolice) {
        this.numeroapolice = numeroapolice;
    }

    public LocalDate getDatainiciocobertura() {
        return datainiciocobertura;
    }

    public void setDatainiciocobertura(LocalDate datainiciocobertura) {
        this.datainiciocobertura = datainiciocobertura;
    }

    public LocalDate getDatafimcobertura() {
        return datafimcobertura;
    }

    public void setDatafimcobertura(LocalDate datafimcobertura) {
        this.datafimcobertura = datafimcobertura;
    }

}