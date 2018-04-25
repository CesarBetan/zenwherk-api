package com.zenwherk.api.endpoint;


import com.zenwherk.api.domain.PlaceScheduleChange;
import com.zenwherk.api.pojo.*;
import com.zenwherk.api.service.PlaceScheduleChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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

    @PUT
    @Path("/place_schedule_change/{uuid}/approval")
    public Response approve(@PathParam("uuid") String uuid) {
        MessageResult result = placeScheduleChangeService.approveReject(uuid, true);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        } else {
            response = Response.ok(result.getMessage()).build();
        }
        return response;
    }

    @PUT
    @Path("/place_schedule_change/{uuid}/rejection")
    public Response reject(@PathParam("uuid") String uuid) {
        MessageResult result = placeScheduleChangeService.approveReject(uuid, false);
        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        } else {
            response = Response.ok(result.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/place_schedule_change")
    public Response getActiveChanges() {
        ListResult<PlaceScheduleChange> result = placeScheduleChangeService.getActiveChanges();
        Response response;
        if(result.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(result.getData().get())).build();
        } else {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        }
        return response;
    }
}
