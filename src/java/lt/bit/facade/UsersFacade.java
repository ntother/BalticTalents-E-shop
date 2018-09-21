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
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
@Path("users")
public class UsersFacade {

    private static final Log LOG = LogFactory.getLog(UsersFacade.class);

    @GET
    @Path("{id}")
    public static Users get(@PathParam("id") Integer id,
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

    public static Users get(@PathParam("id") Integer id) {
        EntityManager em = null;
        try {
            em = EMF.getEntityManager();
            Users user = em.find(Users.class, id);
            return user;
        } finally {
            EMF.returnEntityManager(em);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static List<Users> get(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        return get();
    }

    public static List<Users> get() {
        EntityManager em = null;
        List res;
        try {
            em = EMF.getEntityManager();
            Query q = em.createQuery("select u from Users u");
            res = q.getResultList();
        } finally {
            EMF.returnEntityManager(em);
        }
        return res;
    }

    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public static Users add(Users newUser,
            @Context HttpServletRequest request) throws Exception {
        if (newUser == null) {
            return null;
        }
        return add(newUser);
    }

    public static Users add(Users user) throws Exception {
//         Make sure that id is not specified - it will be set by database
        user.setId(null);
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            List<Users> userList = UsersFacade.get();
            for (Users userFromDB : userList) {
                if (user.getUsername().equals(userFromDB.getUsername())) {
                    return null;
                }
            }
            em.persist(user);
            EMF.commitTransaction(tx);
            return user;
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
    public static Users update(Users updateUser,
            @Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (updateUser == null) {
            return null;
        }
        return update(updateUser);
    }

    public static Users update(Users user) throws Exception {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Users oldUser = em.find(Users.class,
                     user.getId());
            if (oldUser != null) {
                oldUser.update(user);
            }
            EMF.commitTransaction(tx);
            return oldUser;
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
    public static Users remove(@PathParam("id") Integer id,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        Users user = UsersFacade.get(usersId);
        if (user == null || !user.getIsAdmin()) {
            return null;
        }
        if (id == null) {
            LOG.warn("id=null was passed to remove users entity. Skipping.");
            return null;
        }
        return remove(id);
    }

    public static Users remove(Integer id) {
        EntityManager em = null;
        Object tx = null;
        try {
            em = EMF.getEntityManager();
            tx = EMF.getTransaction(em);
            Users user = em.find(Users.class,
                     id);
            if (user == null) {
                LOG.warn("Users entity with id=" + id + " not found.");
                return null;
            }
            em.remove(user);
            EMF.commitTransaction(tx);
            return user;
        } catch (Exception ex) {
            if (tx != null) {
                EMF.rollbackTransaction(tx);
            }
            LOG.error("Failed to remove users entity with id=" + id, ex);
            return null;
        } finally {
            EMF.returnEntityManager(em);
        }
    }
}
