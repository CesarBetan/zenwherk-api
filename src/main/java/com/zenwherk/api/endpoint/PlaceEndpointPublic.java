package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Place;
import com.zenwherk.api.pojo.ListResponse;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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

    @GET
    @Path("/place")
    public Response search(@QueryParam("name") String name, @QueryParam("categories") List<String> categories, @QueryParam("features") List<String> features) {
        ListResult<Place> placeListResult = placeService.searchPlaces(name, categories, features, false, false);
        Response response;
        if(placeListResult.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(placeListResult.getData().get())).build();
        } else {
            response = Response.serverError().entity(new Message("Error de servidor")).build();
        }
        return response;
    }
}
