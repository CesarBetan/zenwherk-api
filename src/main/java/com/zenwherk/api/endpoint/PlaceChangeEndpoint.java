package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceChangeEndpoint {

    @Autowired
    private PlaceChangeService placeChangeService;

    @POST
    @Path("/place_change")
    public Response insert(PlaceChange placeChange) {
        Result<PlaceChange> placeChangeResult = placeChangeService.insert(placeChange);
        Response response;
        if(placeChangeResult.getData().isPresent()){
            response = Response.ok(placeChangeResult.getData().get()).build();
        } else {
            if(placeChangeResult.getErrorCode() == null || placeChangeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeChangeResult.getErrorCode()).entity(placeChangeResult.getMessage()).build();
            }
        }
        return response;
    }

    @PUT
    @Path("/place_change/{uuid}/approval")
    public Response approve(@PathParam("uuid") String uuid) {
        MessageResult result = placeChangeService.approveReject(uuid, true);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        } else {
            response = Response.ok(result.getMessage()).build();
        }
        return response;
    }

    @PUT
    @Path("/place_change/{uuid}/rejection")
    public Response reject(@PathParam("uuid") String uuid) {
        MessageResult result = placeChangeService.approveReject(uuid, false);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        } else {
            response = Response.ok(result.getMessage()).build();
        }
        return response;
    }
}
