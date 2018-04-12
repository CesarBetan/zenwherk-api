package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PasswordRecoveryToken;
import com.zenwherk.api.domain.User;
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
@Path("/v1/public")
@Produces(MediaType.APPLICATION_JSON)
public class UserEndpointPublic {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @POST
    @Path("/user")
    public Response insert(User user) {
        Result<User> userResult = userService.insert(user);
        Response response;
        if(userResult.getData().isPresent()){
            response = Response.ok(userResult.getData().get()).build();
        } else {
            switch (userResult.getErrorCode()){
                case 400:
                    response = Response.status(userResult.getErrorCode()).entity(userResult.getMessage()).build();
                    break;
                default:
                    response = Response.serverError().entity(userResult.getMessage()).build();
                    break;
            }
        }
        return response;
    }

    @POST
    @Path("/user/{uuid}/recovery")
    public Response generatePasswordRecoveryToken(@PathParam("uuid") String uuid) {
        MessageResult result = passwordRecoveryService.generatePasswordRecoveryToken(uuid);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            switch (result.getErrorCode()) {
                case 404:
                    response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
                    break;
                default:
                    response = Response.serverError().entity(result.getMessage()).build();
            }
        } else {
            response = Response.ok(result.getMessage()).build();
        }

        return response;
    }

    @PUT
    @Path("/user/recovery")
    public Response recoverPassword(PasswordRecoveryToken passwordRecoveryToken) {
        MessageResult result = passwordRecoveryService.recoverPassword(passwordRecoveryToken);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            switch (result.getErrorCode()) {
                case 400:
                case 404:
                    response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
                    break;
                default:
                    response = Response.serverError().entity(result.getMessage()).build();
            }
        } else {
            response = Response.ok(result.getMessage()).build();
        }

        return response;
    }
}
