package com.zenwherk.api.endpoint;


import com.zenwherk.api.domain.PlaceSchedule;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceScheduleService;
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
public class PlaceScheduleEndpointPublic {

    @Autowired
    private PlaceScheduleService placeScheduleService;

    @GET
    @Path("/place_schedule/{uuid}")
    public Response getPlaceFeatureByUuid(@PathParam("uuid") String uuid) {
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.getPlaceScheduleByUuid(uuid, false);
        Response response;
        if(placeScheduleResult.getData().isPresent()) {
            response = Response.ok(placeScheduleResult.getData().get()).build();
        } else {
            response = Response.status(placeScheduleResult.getErrorCode()).entity(placeScheduleResult.getMessage()).build();
        }
        return response;
    }

}
