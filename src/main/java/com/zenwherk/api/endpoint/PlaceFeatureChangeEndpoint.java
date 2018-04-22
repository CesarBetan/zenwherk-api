package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceFeatureChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceFeatureChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
