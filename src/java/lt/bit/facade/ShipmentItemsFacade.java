/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.facade;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
import lt.bit.beans.Shipment;
import lt.bit.beans.ShipmentItems;
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
@Path("shipmentitems")
public class ShipmentItemsFacade {

    private static final Log LOG = LogFactory.getLog(ShipmentItemsFacade.class);

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static ShipmentItems get(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to find shipments item entity. Returning null.");
            return null;
        }
        return get(id);
    }

    public static ShipmentItems get(Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            ShipmentItems b_i = em.find(ShipmentItems.class, id);
            if (b_i == null) {
                return null;
            }
            return b_i;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<ShipmentItems> getForShipment(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to find shipments item entity. Returning null.");
            return null;
        }
        return getForShipment(id);
    }

    public static List<ShipmentItems> getForShipment(Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            Shipment shipment = em.find(Shipment.class, id);
            if (shipment == null) {
                LOG.warn("Shipment entity with id=" + id + " not found. Returning null.");
                return null;
            }
            em.refresh(shipment);
            return shipment.getShipmentItemsList();
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public static List<ShipmentItems> add(@Valid List<ShipmentItems> shipmentItems,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (shipmentItems == null) {
            return null;
        }
        Shipment shipment = new Shipment();
        shipment.setDate(new Date());
        ShipmentsFacade.add(shipment);
        return add(shipmentItems, shipment);
    }

    public static List<ShipmentItems> add(List<ShipmentItems> shipmentsList, Shipment shipment) throws Exception {
        System.out.println(shipmentsList);
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);

            for (ShipmentItems item : shipmentsList) {
                if (item == null) {
                    return null;
                }
                // Make sure that id is not specified - it will be set by database
                item.setId(null);
                Product product = em.find(Product.class, item.getProduct().getId());
                if (product == null) {
                    return null;
                }
                product.setQuantity(product.getQuantity() + item.getQuantity());
                ProductsFacade.update(product);

                if (shipment == null) {
                    return null;
                }
                item.setShipment(shipment);
                item.setProduct(product);
                em.persist(item);
            }
            EMF.commitTransaction(tx);
            return shipmentsList;
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
    public static ShipmentItems update(ShipmentItems shipmentItem,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (shipmentItem == null) {
            return null;
        }
        return update(shipmentItem);
    }

    public static ShipmentItems update(ShipmentItems shipmentItems) throws Exception {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            ShipmentItems oldShipmentItem = em.find(ShipmentItems.class, shipmentItems.getId());
            if (oldShipmentItem == null) {
                return null;
            }
            oldShipmentItem.update(shipmentItems);
            EMF.commitTransaction(tx);
            return oldShipmentItem;
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
    public static ShipmentItems remove(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(userId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to remove shipments items entity. Skipping.");
            return null;
        }
        return remove(id);
    }

    public static ShipmentItems remove(Integer id) {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            ShipmentItems shipmentItem = em.find(ShipmentItems.class, id);
            if (shipmentItem == null) {
                LOG.warn("Shipments item entity with id=" + id + " not found.");
                return null;
            }
            shipmentItem.getShipment().getShipmentItemsList().remove(shipmentItem);
            em.remove(shipmentItem);
            EMF.commitTransaction(tx);
            return shipmentItem;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            LOG.error("Failed to remove shipments item entity with id=" + id, ex);
            return null;
        } finally {
            EMF.returnEntityManager(em);
        }
    }
}
