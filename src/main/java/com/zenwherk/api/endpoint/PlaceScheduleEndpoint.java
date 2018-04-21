package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceSchedule;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PlaceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceScheduleEndpoint {

    @Autowired
    private PlaceScheduleService placeScheduleService;

    @POST
    @Path("/place_schedule")
    public Response insert(PlaceSchedule placeSchedule) {
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.insert(placeSchedule);
        Response response;
        if(placeScheduleResult.getData().isPresent()) {
            response = Response.ok(placeScheduleResult.getData().get()).build();
        } else {
            if(placeScheduleResult.getErrorCode() == null || placeScheduleResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeScheduleResult.getErrorCode()).entity(placeScheduleResult.getMessage()).build();
            }
        }
        return response;
    }

    @DELETE
    @Path("/place_schedule/{uuid}")
    public Response delete(@PathParam("uuid") String uuid, User user) {
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.deletePlaceSchedule(uuid, user);
        Response response;
        if(placeScheduleResult.getData().isPresent()) {
            response = Response.ok(placeScheduleResult.getData().get()).build();
        } else {
            if(placeScheduleResult.getErrorCode() == null || placeScheduleResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeScheduleResult.getErrorCode()).entity(placeScheduleResult.getMessage()).build();
            }
        }
        return response;
    }
}
