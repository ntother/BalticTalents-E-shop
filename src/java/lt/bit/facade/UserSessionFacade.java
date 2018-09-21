/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.facade;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lt.bit.beans.Basket;
import lt.bit.beans.Users;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Nikanoras
 */
@Path("usersession")
public class UserSessionFacade {

    private static final Log LOG = LogFactory.getLog(UserSessionFacade.class);

    @Path("getUserId")
    @GET
    public static Integer getSessionId(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer id = (Integer) session.getAttribute("userId");
        return id;
    }

    @Path("getUsername")
    @GET
    public static String getSessionUsername(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        return username;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public static Users loginUserSession(Users user,
            @Context HttpServletRequest request) throws Exception {
        List<Users> usersList = UsersFacade.get();
        Users verifiedUser = null;
        for (Users DBuser : usersList) {
            if (user.getUsername().equals(DBuser.getUsername()) && user.getPassword().equals(DBuser.getPassword())) {
                verifiedUser = DBuser;
            }
        }
        LOG.info(verifiedUser);
        if (verifiedUser == null) {
            return null;
        }
        HttpSession session = request.getSession();
        session.setAttribute("username", verifiedUser.getUsername());
        session.setAttribute("userId", verifiedUser.getId());
        // when users logins, basket id is automatically assigned to that basket
        Integer unfinishedBasketsId = BasketSessionFacade.getUnfinishedUsersBasketSession(verifiedUser.getId());
        if (unfinishedBasketsId != null) {
            session.setAttribute("basketId", unfinishedBasketsId);
            return verifiedUser;
        }
        Integer basketId = (Integer) session.getAttribute("basketId");
        LOG.info(basketId);
//        if (basketId == null) {
//            return null;
//        }
//        Basket basket = BasketsFacade.get(basketId);
//        basket.setUser(verifiedUser);
//        BasketsFacade.update(basket);
        return verifiedUser;
    }

    @DELETE
    @Path("logout")
    public static void destroySession(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return;
        }
        session.invalidate();

    }

}
