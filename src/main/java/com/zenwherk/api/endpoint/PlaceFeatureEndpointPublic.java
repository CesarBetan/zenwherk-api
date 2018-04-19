package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceFeature;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1/public")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceFeatureEndpointPublic {

    @Autowired
    private PlaceFeatureService placeFeatureService;

    @GET
    @Path("/place_feature/{uuid}")
    public Response getPlaceFeatureByUuid(@PathParam("uuid") String uuid) {
        Result<PlaceFeature> placeFeatureResult = placeFeatureService.getPlaceFeatureByUuid(uuid, false);
        Response response;
        if(placeFeatureResult.getData().isPresent()) {
            response = Response.ok(placeFeatureResult.getData().get()).build();
        } else {
            response = Response.status(placeFeatureResult.getErrorCode()).entity(placeFeatureResult.getMessage()).build();
        }
        return response;
    }
}
