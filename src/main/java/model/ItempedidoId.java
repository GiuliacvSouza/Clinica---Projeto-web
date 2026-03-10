package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ItempedidoId implements Serializable {
    private static final long serialVersionUID = 9148983576092388547L;
    @Column(name = "idmaterial", nullable = false)
    private Integer idmaterial;

    @Column(name = "idpedido", nullable = false)
    private Integer idpedido;

    public Integer getIdmaterial() {
        return idmaterial;
    }

    public void setIdmaterial(Integer idmaterial) {
        this.idmaterial = idmaterial;
    }

    public Integer getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(Integer idpedido) {
        this.idpedido = idpedido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItempedidoId entity = (ItempedidoId) o;
        return Objects.equals(this.idmaterial, entity.idmaterial) &&
                Objects.equals(this.idpedido, entity.idpedido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idmaterial, idpedido);
    }
}