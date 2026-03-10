package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contatoemergencia")
public class Contatoemergencia {
    @Id
    @Column(name = "contatoemergencia", nullable = false)
    private Integer id;

    @Column(name = "primeironome", length = 100)
    private String primeironome;

    @Column(name = "ultimonome", length = 100)
    private String ultimonome;

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

}