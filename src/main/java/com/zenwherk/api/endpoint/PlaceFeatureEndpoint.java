package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceFeatureEndpoint {

    @Autowired
    private PlaceFeatureService placeFeatureService;

    @POST
    @Path("/place_feature")
    public Response insert(PlaceFeature placeFeature) {
        Result<PlaceFeature> placeFeatureResult = placeFeatureService.insert(placeFeature);
        Response response;
        if(placeFeatureResult.getData().isPresent()) {
            response = Response.ok(placeFeatureResult.getData().get()).build();
        } else {
            if(placeFeatureResult.getErrorCode() == null || placeFeatureResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeFeatureResult.getErrorCode()).entity(placeFeatureResult.getMessage()).build();
            }
        }
        return response;
    }

    @PUT
    @Path("/place_feature/{uuid}")
    public Response update(@PathParam("uuid") String uuid, PlaceFeature placeFeature) {
        Result<PlaceFeature> placeFeatureResult = placeFeatureService.update(uuid, placeFeature);
        Response response;
        if (placeFeatureResult.getData().isPresent()) {
            response = Response.ok(placeFeatureResult.getData().get()).build();
        } else {
            if(placeFeatureResult.getErrorCode() == null || placeFeatureResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeFeatureResult.getErrorCode()).entity(placeFeatureResult.getMessage()).build();
            }
        }
        return response;
    }

    @DELETE
    @Path("/place_feature/{uuid}")
    public Response delete(@PathParam("uuid") String uuid, User user) {
        Result<PlaceFeature> placeFeatureResult = placeFeatureService.deletePlaceFeature(uuid, user);
        Response response;
        if(placeFeatureResult.getData().isPresent()) {
            response = Response.ok(placeFeatureResult.getData().get()).build();
        } else {
            if(placeFeatureResult.getErrorCode() == null || placeFeatureResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeFeatureResult.getErrorCode()).entity(placeFeatureResult.getMessage()).build();
            }
        }
        return response;
    }
}
