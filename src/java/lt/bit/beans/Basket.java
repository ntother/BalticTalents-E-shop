/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author Nikanoras
 */
@Entity
@Table(name = "baskets")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Baskets.findAll", query = "SELECT b FROM Basket b")
    , @NamedQuery(name = "Baskets.findById", query = "SELECT b FROM Basket b WHERE b.id = :id")
    , @NamedQuery(name = "Baskets.findByPaymentDate", query = "SELECT b FROM Basket b WHERE b.paymentDate = :paymentDate")})
public class Basket implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Date paymentDate;
    private List<BasketItems> basketItemsList;
    private Users user;

    public Basket() {
    }

    public Basket(Integer id) {
        this.id = id;
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

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "basket")
    @Fetch(FetchMode.SELECT)
    @JsonbTransient
    public List<BasketItems> getBasketItemsList() {
        return basketItemsList;
    }

    public void setBasketItemsList(List<BasketItems> basketItemsList) {
        this.basketItemsList = basketItemsList;
    }

    @JoinColumn(name = "users_id", referencedColumnName = "id")
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JsonbTransient
    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
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
        if (!(object instanceof Basket)) {
            return false;
        }
        Basket other = (Basket) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "lt.bit.Baskets[ id=" + id + " ]";
    }

    public void update(Basket baskets) {
        if (baskets == null) {
            return;
        }
        this.setPaymentDate(baskets.getPaymentDate());
        if (this.getUser() == null) {
            this.setUser(baskets.getUser());
        }
    }

}
