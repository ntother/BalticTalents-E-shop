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
import lt.bit.beans.Shipment;
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
public class ShipmentsFacade {

    private static final Log LOG = LogFactory.getLog(ShipmentsFacade.class);

    @GET
    @Path("{id}")
    public static Shipment get(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to find products entity. Returning null.");
            return null;
        }
        return get(id);
    }

    public static Shipment get(@PathParam("id") Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            Shipment shipemnt = em.find(Shipment.class, id);
            return shipemnt;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Shipment> get(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        return get();
    }

    public static List<Shipment> get() {
        EntityManager em = null;
        List res;
        try {
            em = EMF.getEntityManager();
            Query q = em.createQuery("select p from Shipment p");
            res = q.getResultList();
        } finally {
            EMF.returnEntityManager(em);
        }
        return res;
    }

    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public static Shipment add(Shipment shipment,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (shipment == null) {
            return null;
        }
        return add(shipment);
    }

    public static Shipment add(Shipment shipment) throws Exception {
        // Make sure that id is not specified - it will be set by database
        shipment.setId(null);
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            em.persist(shipment);
            EMF.commitTransaction(tx);
            return shipment;
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
    public static Shipment update(Shipment shipment,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (shipment == null) {
            return null;
        }
        return update(shipment);
    }

    public static Shipment update(Shipment shipment) throws Exception {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Shipment oldShipment = em.find(Shipment.class, shipment.getId());
            if (oldShipment == null) {
                return null;
            }
            oldShipment.update(shipment);
            EMF.commitTransaction(tx);
            return oldShipment;
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
    public static Shipment remove(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to find products entity. Returning null.");
            return null;
        }
        return remove(id);
    }

    public static Shipment remove(Integer id) {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Shipment shipment = em.find(Shipment.class, id);
            if (shipment == null) {
                LOG.warn("Shipments entity with id=" + id + " not found.");
                return null;
            }
            em.remove(shipment);
            EMF.commitTransaction(tx);
            return shipment;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            LOG.error("Failed to remove shipment entity with id=" + id, ex);
            return null;
        } finally {
            EMF.returnEntityManager(em);
        }
    }
}
