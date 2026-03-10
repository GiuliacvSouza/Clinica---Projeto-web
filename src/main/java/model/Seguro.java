package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "seguro")
public class Seguro {
    @Id
    @Column(name = "idseguro", nullable = false)
    private Integer id;

    @Column(name = "nomeseguro", length = 100)
    private String nomeseguro;

    @Column(name = "tipoplano", length = 100)
    private String tipoplano;

    @Column(name = "codigoplano", length = 100)
    private String codigoplano;

    @Column(name = "contactoseguradora", length = 150)
    private String contactoseguradora;

    @Column(name = "validoate")
    private LocalDate validoate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeseguro() {
        return nomeseguro;
    }

    public void setNomeseguro(String nomeseguro) {
        this.nomeseguro = nomeseguro;
    }

    public String getTipoplano() {
        return tipoplano;
    }

    public void setTipoplano(String tipoplano) {
        this.tipoplano = tipoplano;
    }

    public String getCodigoplano() {
        return codigoplano;
    }

    public void setCodigoplano(String codigoplano) {
        this.codigoplano = codigoplano;
    }

    public String getContactoseguradora() {
        return contactoseguradora;
    }

    public void setContactoseguradora(String contactoseguradora) {
        this.contactoseguradora = contactoseguradora;
    }

    public LocalDate getValidoate() {
        return validoate;
    }

    public void setValidoate(LocalDate validoate) {
        this.validoate = validoate;
    }

}