/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.beans;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Nikanoras
 */
@Entity
@Table(name = "shipment_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ShipmentItems.findAll", query = "SELECT s FROM ShipmentItems s")
    , @NamedQuery(name = "ShipmentItems.findById", query = "SELECT s FROM ShipmentItems s WHERE s.id = :id")
    , @NamedQuery(name = "ShipmentItems.findByQuantity", query = "SELECT s FROM ShipmentItems s WHERE s.quantity = :quantity")})
public class ShipmentItems implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private int quantity;
    private Product product;
    private Shipment shipment;

    public ShipmentItems() {
    }

    public ShipmentItems(Integer id) {
        this.id = id;
    }

    public ShipmentItems(Integer id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JoinColumn(name = "products_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @JoinColumn(name = "shipments_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ShipmentItems)) {
            return false;
        }
        ShipmentItems other = (ShipmentItems) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "lt.bit.ShipmentItems[ id=" + id + " ]";
    }
    
    public void update(ShipmentItems items) {
        if(items == null) {
            return;
        }
        this.setQuantity(items.getQuantity());
    }
    
}
