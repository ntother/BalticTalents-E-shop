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
@Table(name = "basket_items")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BasketItems.findAll", query = "SELECT b FROM BasketItems b")
    , @NamedQuery(name = "BasketItems.findById", query = "SELECT b FROM BasketItems b WHERE b.id = :id")
    , @NamedQuery(name = "BasketItems.findByPrice", query = "SELECT b FROM BasketItems b WHERE b.price = :price")
    , @NamedQuery(name = "BasketItems.findByQuantity", query = "SELECT b FROM BasketItems b WHERE b.quantity = :quantity")})
public class BasketItems implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer price;
    private Integer quantity;
    private Basket basket;
    private Product product;

    public BasketItems() {
    }

    public BasketItems(Integer id) {
        this.id = id;
    }

    public BasketItems(Integer id, Integer price, Integer quantity) {
        this.id = id;
        this.price = price;
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
    @Column(name = "price")
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @JoinColumn(name = "baskets_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    @JoinColumn(name = "products_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
        if (!(object instanceof BasketItems)) {
            return false;
        }
        BasketItems other = (BasketItems) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "lt.bit.BasketItems[ id=" + id + " ]";
    }
    
    public void update(BasketItems basketItems){
        if(basketItems == null){
            return;
        }
        this.setPrice(basketItems.getPrice());
        this.setQuantity(basketItems.getQuantity());
    }
    
}
