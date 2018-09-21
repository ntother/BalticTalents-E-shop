/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author Nikanoras
 */
@Entity
@Table(name = "shipments")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Shipments.findAll", query = "SELECT s FROM Shipment s")
    , @NamedQuery(name = "Shipments.findById", query = "SELECT s FROM Shipment s WHERE s.id = :id")
    , @NamedQuery(name = "Shipments.findByDate", query = "SELECT s FROM Shipment s WHERE s.date = :date")
    , @NamedQuery(name = "Shipments.findByInfo", query = "SELECT s FROM Shipment s WHERE s.info = :info")})
public class Shipment implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date date;
    private String info;
    private List<ShipmentItems> shipmentItemsList;

    public Shipment() {
    }

    public Shipment(Integer id) {
        this.id = id;
    }

    public Shipment(Date date) {
        this.date = date;
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
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    @JsonbDateFormat("yyyy/MM/dd HH:mm:ss")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Size(max = 100)
    @Column(name = "info")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shipment")
    @Fetch(FetchMode.SELECT)
    @JsonbTransient
    public List<ShipmentItems> getShipmentItemsList() {
        return shipmentItemsList;
    }

    public void setShipmentItemsList(List<ShipmentItems> shipmentItemsList) {
        this.shipmentItemsList = shipmentItemsList;
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
        if (!(object instanceof Shipment)) {
            return false;
        }
        Shipment other = (Shipment) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "lt.bit.Shipments[ id=" + id + " ]";
    }
    
    public void update(Shipment shipment) {
        if (shipment == null) {
            return;
        }
        this.setDate(shipment.getDate());
        this.setInfo(shipment.getInfo());
    }

}
