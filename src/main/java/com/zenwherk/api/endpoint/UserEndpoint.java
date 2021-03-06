package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResponse;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PasswordRecoveryService;
import com.zenwherk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class UserEndpoint {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @GET
    @Path("/user")
    public Response searchUsers(@QueryParam("q") String query) {
        ListResult<User> userListResult = userService.searchUsers(query, false, false);
        Response response;
        if(userListResult.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(userListResult.getData().get())).build();
        } else {
            response = Response.status(userListResult.getErrorCode()).entity(userListResult.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/user/{uuid}")
    public Response getUserByUuid(@PathParam("uuid") String uuid) {
        Result<User> userResult = userService.getUserByUuid(uuid, false, false);
        Response response;
        if(userResult.getData().isPresent()){
            response = Response.ok(userResult.getData().get()).build();
        } else {
            response = Response.status(userResult.getErrorCode()).entity(userResult.getMessage()).build();
        }
        return response;
    }

    @PUT
    @Path("/user/{uuid}")
    public Response update(@PathParam("uuid") String uuid, User user) {
        Result<User> userResult = userService.update(uuid, user);
        Response response;
        if(userResult.getData().isPresent()) {
            response = Response.ok(userResult.getData().get()).build();
        } else {
            switch (userResult.getErrorCode()) {
                case 400:
                case 404:
                    response = Response.status(userResult.getErrorCode()).entity(userResult.getMessage()).build();
                    break;
                default:
                    response = Response.serverError().entity(userResult.getMessage()).build();
            }
        }
        return response;
    }
}
