/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.bit.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

/**
 *
 * @author Nikanoras
 */
@Path("basketSession")
public class BasketSessionFacade {

    @GET
    public static Integer getSessionBasket(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer basketId = (Integer) session.getAttribute("basketId");
        return basketId;
    }

    @Path("basket")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Basket getBasket(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer basketId = (Integer) session.getAttribute("basketId");
        Basket basket = BasketsFacade.get(basketId);
        return basket;
    }

    // return last unused basket 
    @GET
    @Path("unfinishedBasketSession")
    public static Integer getUnfinishedUsersBasketSession(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer usersId = (Integer) session.getAttribute("userId");
        System.out.println(usersId);
        if (usersId == null) {
            return null;
        }
        return getUnfinishedUsersBasketSession(usersId);
    }

    public static Integer getUnfinishedUsersBasketSession(Integer usersId) {
        Users user = UsersFacade.get(usersId);
        List<Basket> basketList = BasketsFacade.get();
        List<Basket> usersUnfinishedBasketList = new ArrayList<>();
        basketList.stream().filter((basket) -> (basket.getUser() != null)).filter((basket) -> (Objects.equals(basket.getUser().getId(), user.getId()) && basket.getPaymentDate() == null)).forEachOrdered((basket) -> {
            usersUnfinishedBasketList.add(basket);
        });
        if (usersUnfinishedBasketList.isEmpty()) {
            return null;
        }
        // returning latest unfinished basket
        return usersUnfinishedBasketList.get(usersUnfinishedBasketList.size() - 1).getId();
    }

    @POST
    public static boolean createBasketSession(@Context HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        if (((Integer) session.getAttribute("basketId")) == null) {
            Basket basket = new Basket();
            BasketsFacade.add(basket);
            session.setAttribute("basketId", basket.getId());
            return true;
        } else {
            return false;
        }
    }

    @DELETE
    public static void destroySession(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("basketId", null);

    }

}
