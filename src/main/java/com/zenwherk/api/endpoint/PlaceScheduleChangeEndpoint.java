package com.zenwherk.api.endpoint;


import com.zenwherk.api.domain.PlaceScheduleChange;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceScheduleChangeService;
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
public class PlaceScheduleChangeEndpoint {

    @Autowired
    private PlaceScheduleChangeService placeScheduleChangeService;

    @POST
    @Path("/place_schedule_change")
    public Response insert(PlaceScheduleChange placeScheduleChange) {
        Result<PlaceScheduleChange> placeScheduleChangeResult = placeScheduleChangeService.insert(placeScheduleChange);
        Response response;
        if(placeScheduleChangeResult.getData().isPresent()){
            response = Response.ok(placeScheduleChangeResult.getData().get()).build();
        } else {
            if(placeScheduleChangeResult.getErrorCode() == null || placeScheduleChangeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeScheduleChangeResult.getErrorCode()).entity(placeScheduleChangeResult.getMessage()).build();
            }
        }
        return response;
    }
}
