package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Favorite;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class FavoriteEndpoint {

    @Autowired
    private FavoriteService favoriteService;

    @POST
    @Path("/favorite")
    public Response insert(Favorite favorite) {
        Result<Favorite> favoriteResult = favoriteService.insert(favorite);
        Response response;
        if(favoriteResult.getData().isPresent()) {
            response = Response.ok(favoriteResult.getData().get()).build();
        } else {
            if(favoriteResult.getErrorCode() == null || favoriteResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(favoriteResult.getErrorCode()).entity(favoriteResult.getMessage()).build();
            }
        }
        return response;
    }

    @DELETE
    @Path("/favorite")
    public Response delete(Favorite favorite) {
        MessageResult result = favoriteService.deleteByUserUuidAndPlaceUuid(favorite);
        Response response;

        if(result.getErrorCode() == null || result.getErrorCode() < 1) {
            response = Response.ok(result.getMessage()).build();
        } else {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        }

        return response;
    }
}
