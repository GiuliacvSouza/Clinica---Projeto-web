package model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "doenca")
public class Doenca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddoenca", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "observacaoclinica", length = Integer.MAX_VALUE)
    private String observacaoclinica;

    @ColumnDefault("true")
    @Column(name = "ativa")
    private Boolean ativa;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getObservacaoclinica() {
        return observacaoclinica;
    }

    public void setObservacaoclinica(String observacaoclinica) {
        this.observacaoclinica = observacaoclinica;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

}