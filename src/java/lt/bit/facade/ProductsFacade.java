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
import lt.bit.beans.Product;
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
@Path("products")
public class ProductsFacade {

    private static final Log LOG = LogFactory.getLog(ProductsFacade.class);

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static Product get(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        if (id == null) {
            LOG.warn("id=null was passed to find products entity. Returning null.");
            return null;
        }
        return get(id);
    }

    public static Product get(@PathParam("id") Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            Product product = em.find(Product.class, id);
            return product;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Product> get(@Context HttpServletRequest request) {
        return get();
    }

    public static List<Product> get() {
        EntityManager em = null;
        List res;
        try {
            em = EMF.getEntityManager();
            Query q = em.createQuery("select p from Product p");
            res = q.getResultList();
        } finally {
            EMF.returnEntityManager(em);
        }
        return res;
    }

    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public static Product add(Product products,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        return add(products);
    }

    public static Product add(Product products) throws Exception {
        // Make sure that id is not specified - it will be set by database
        products.setId(null);
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            em.persist(products);
            EMF.commitTransaction(tx);
            return products;
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
    public static Product update(Product products,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        return update(products);
    }

    public static Product update(Product products) throws Exception {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Product productFromDB = em.find(Product.class, products.getId());
            if (productFromDB != null) {
                productFromDB.update(products);
            }
            EMF.commitTransaction(tx);
            return productFromDB;
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
    public static Product remove(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to remove products entity. Skipping.");
            return null;
        }
        return remove(id);
    }

    public static Product remove(Integer id) {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Product product = em.find(Product.class, id);
            if (product == null) {
                LOG.warn("Products entity with id=" + id + " not found.");
                return null;
            }
            em.remove(product);
            EMF.commitTransaction(tx);
            return product;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            LOG.error("Failed to remove products entity with id=" + id, ex);
            return null;
        } finally {
            EMF.returnEntityManager(em);
        }
    }
}
