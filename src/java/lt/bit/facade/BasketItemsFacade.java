/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.facade;

import java.util.List;
import javax.persistence.EntityManager;
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
import lt.bit.beans.BasketItems;
import lt.bit.beans.Basket;
import lt.bit.beans.Product;
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
@Path("basketitems")
public class BasketItemsFacade {

    private static final Log LOG = LogFactory.getLog(BasketItemsFacade.class);

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static BasketItems get(@PathParam("id") Integer basketItemsId,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer basketId = (Integer) session.getAttribute("basketId");
        if (basketId == null) {
            return null;
        } else {
            return get(basketItemsId);
        }
    }

    public static BasketItems get(Integer basketItemsId) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            BasketItems b_i = em.find(BasketItems.class, basketItemsId);
            return b_i;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<BasketItems> getForBasket(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        if (id == null) {
            return null;
        }
        return getForBasket(id);
    }

    public static List<BasketItems> getForBasket(Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            Basket basket = em.find(Basket.class, id);
            if (basket == null) {
                LOG.warn("Basket entity with id=" + id + " not found. Returning null.");
                return null;
            }
            em.refresh(basket);
            return basket.getBasketItemsList();
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @POST
    @Path("{basketId}/{productId}/{userId}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public static BasketItems add(BasketItems b_i,
            @PathParam("basketId") Integer basketId,
            @PathParam("productId") Integer productId,
            @PathParam("userId") Integer userId,
            @Context HttpServletRequest request) throws Exception {
        if (basketId == null || productId == null) {
            return null;
        }
        if (b_i.getQuantity() <= 0) {
            return null;
        }
        return add(b_i, basketId, productId, userId);
    }

    public static BasketItems add(BasketItems b_i, Integer basketsId, Integer productId, Integer userId) throws Exception {
        // Make sure that id is not specified - it will be set by database
        b_i.setId(null);
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Basket basket = em.find(Basket.class, basketsId);
            Product product = em.find(Product.class, productId);
            if (basket == null || product == null) {
                return null;
            }
            if (userId != null && basket.getUser() != null) {
                Users user = UsersFacade.get(userId);
                basket.setUser(user);
            }
            List<BasketItems> basketItemsList = basket.getBasketItemsList();
            b_i.setProduct(product);
            for (BasketItems basketItem : basketItemsList) {
                if (b_i.getProduct().getName().equals(basketItem.getProduct().getName())) {
                    basketItem.setQuantity(basketItem.getQuantity() + b_i.getQuantity());
//                    em.persist(basketItem);
//                    EMF.commitTransaction(tx);
                    b_i = basketItem;
//                    return basketItem;
                }
            }
            if (b_i.getBasket() == null) {
                b_i.setBasket(basket);
            }
            em.persist(b_i);
            EMF.commitTransaction(tx);
            return b_i;
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
    @Path("{basketId}")
    public static BasketItems update(BasketItems basketItem,
            @PathParam("basketId") Integer basketId,
            @Context HttpServletRequest request) throws Exception {
        // check if baskets exists in the session, if not return null;
        if (basketId == null || basketItem == null) {
            return null;
        }
        return update(basketItem);
    }

    public static BasketItems update(BasketItems basketItem) throws Exception {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            BasketItems oldBasketItem = em.find(BasketItems.class, basketItem.getId());
            if (oldBasketItem == null) {
                return null;
            }
            oldBasketItem.update(basketItem);
            EMF.commitTransaction(tx);
            return oldBasketItem;
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
    @Path("{id}/{basketId}")
    public static BasketItems remove(@PathParam("id") Integer id,
            @PathParam("basketId") Integer basketId,
            @Context HttpServletRequest request) {
        if (basketId == null) {
            return null;
        }
        return remove(id, basketId);
    }

    public static BasketItems remove(Integer basketItemId, Integer basketId) {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Basket basket = BasketsFacade.get(basketId);
            BasketItems b_i = em.find(BasketItems.class, basketItemId);

            // checking if the basket of users session contains item
            // to guarenty the work will users basket only
            if (!basket.getBasketItemsList().contains(b_i) || b_i == null) {
                return null;
            }
            b_i.getBasket().getBasketItemsList().remove(b_i);
            em.remove(b_i);
            EMF.commitTransaction(tx);
            return b_i;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            LOG.error("Failed to remove baskets item entity with id=" + basketItemId, ex);
            return null;
        } finally {
            EMF.returnEntityManager(em);
        }
    }
}
