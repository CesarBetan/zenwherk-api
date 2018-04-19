package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Place;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceService;
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
public class PlaceEndpointPublic {

    @Autowired
    private PlaceService placeService;

    @GET
    @Path("/place/{uuid}")
    public Response getPlaceByUuid(@PathParam("uuid") String uuid) {
        Result<Place> placeResult = placeService.getPlaceByUuid(uuid, false, false);
        Response response;
        if(placeResult.getData().isPresent()) {
            response = Response.ok(placeResult.getData().get()).build();
        } else {
            response = Response.status(placeResult.getErrorCode()).entity(placeResult.getMessage()).build();
        }
        return response;
    }
}
