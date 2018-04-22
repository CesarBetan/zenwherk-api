package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceFeatureChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceFeatureChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceFeatureChangeEndpoint {

    @Autowired
    private PlaceFeatureChangeService placeFeatureChangeService;

    @POST
    @Path("/place_feature_change")
    public Response insert(PlaceFeatureChange placeFeatureChange) {
        Result<PlaceFeatureChange> placeFeatureChangeResult = placeFeatureChangeService.insert(placeFeatureChange);
        Response response;
        if(placeFeatureChangeResult.getData().isPresent()){
            response = Response.ok(placeFeatureChangeResult.getData().get()).build();
        } else {
            if(placeFeatureChangeResult.getErrorCode() == null || placeFeatureChangeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeFeatureChangeResult.getErrorCode()).entity(placeFeatureChangeResult.getMessage()).build();
            }
        }
        return response;
    }

    @PUT
    @Path("/place_feature_change/{uuid}/approval")
    public Response approve(@PathParam("uuid") String uuid) {
        MessageResult result = placeFeatureChangeService.approveReject(uuid, true);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        } else {
            response = Response.ok(result.getMessage()).build();
        }
        return response;
    }

    @PUT
    @Path("/place_feature_change/{uuid}/rejection")
    public Response reject(@PathParam("uuid") String uuid) {
        MessageResult result = placeFeatureChangeService.approveReject(uuid, false);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        } else {
            response = Response.ok(result.getMessage()).build();
        }
        return response;
    }
}
