/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.facade;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lt.bit.beans.Basket;
import lt.bit.beans.BasketItems;
import lt.bit.beans.Product;
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
@Path("basket")
public class BasketsFacade {

    private static final Log LOG = LogFactory.getLog(Basket.class);

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static Basket get(@PathParam("id") Integer basketsId,
            @Context HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        String username = (String) session.getAttribute("username");
//        if (username == null) {
//            return null;
//        } else {
        if (basketsId == null) {
            return null;
        }
        return get(basketsId);
//        }
    }

    public static Basket get(@PathParam("id") Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            Basket baskets = em.find(Basket.class, id);
            return baskets;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Basket> get(@Context HttpServletRequest request) {
//        HttpSession session = request.getSession();
//        String username = (String) session.getAttribute("username");
//        if (username == null) {
//            return null;
//        } else {
        return get();
//        }
    }

    public static List<Basket> get() {
        EntityManager em = null;
        List res;
        try {
            em = EMF.getEntityManager();
            Query q = em.createQuery("select b from Basket b");
            res = q.getResultList();
        } finally {
            EMF.returnEntityManager(em);
        }
        return res;
    }

    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public static Basket add(Basket basket,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null || basket == null) {
            return null;
        }
        return add(basket);
    }

    public static Basket add(Basket baskets) throws Exception {
//         Make sure that id is not specified - it will be set by database
        baskets.setId(null);
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            em.persist(baskets);
            EMF.commitTransaction(tx);
            return baskets;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            throw ex;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static Basket update(Basket basket,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || basket == null) {
            return null;
        }
        basket.setUser(user);
        return update(basket);
    }

    public static Basket update(Basket basket) throws Exception {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Basket basketFromDB = em.find(Basket.class, basket.getId());
            if (basketFromDB == null) {
                return null;
            }

            // Updating products quantity
            List<BasketItems> basketItemsList = basketFromDB.getBasketItemsList();
            if (basketItemsList.isEmpty()) {
                return null;
            }
            for (BasketItems basketItems : basketItemsList) {
                Product product = ProductsFacade.get(basketItems.getProduct().getId());
                Integer productsQuantity = product.getQuantity();
                Integer itemsQuantity = basketItems.getQuantity();
                if (productsQuantity < itemsQuantity) {
                    return null;
                }
                product.setQuantity(productsQuantity - itemsQuantity);
                ProductsFacade.update(product);
            }
            basketFromDB.update(basket);
            EMF.commitTransaction(tx);
            return basketFromDB;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            throw ex;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @DELETE
    @Path("{id}")
    public static Basket remove(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to remove baskets entity. Skipping.");
            return null;
        }
        return remove(id);
    }

    public static Basket remove(Integer id) {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Basket baskets = em.find(Basket.class, id);
            if (baskets == null) {
                LOG.warn("Basket entity with id=" + id + " not found.");
                return null;
            }
            em.remove(baskets);
            EMF.commitTransaction(tx);
            return baskets;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            LOG.error("Failed to remove baskets entity with id=" + id, ex);
            return null;
        } finally {
            EMF.returnEntityManager(em);
        }
    }
}
