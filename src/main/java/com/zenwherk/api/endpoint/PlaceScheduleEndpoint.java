package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.PlaceSchedule;
import com.zenwherk.api.domain.User;
import com.zenwherk.api.pojo.ListResponse;
import com.zenwherk.api.pojo.ListResult;
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

    @PUT
    @Path("/place_schedule/{uuid}")
    public Response update(@PathParam("uuid") String uuid, PlaceSchedule placeSchedule) {
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.update(uuid, placeSchedule);
        Response response;
        if (placeScheduleResult.getData().isPresent()) {
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

    @PUT
    @Path("/place_schedule/{uuid}/approval")
    public Response approve(@PathParam("uuid") String uuid) {
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.approveOrReject(uuid, true);
        Response response;
        if (placeScheduleResult.getData().isPresent()) {
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

    @PUT
    @Path("/place_schedule/{uuid}/rejection")
    public Response reject(@PathParam("uuid") String uuid) {
        Result<PlaceSchedule> placeScheduleResult = placeScheduleService.approveOrReject(uuid, false);
        Response response;
        if (placeScheduleResult.getData().isPresent()) {
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

    @GET
    @Path("/place_schedule")
    public Response searchToBeApprovedOrDeleted(@QueryParam("q") String query) {
        ListResult<PlaceSchedule> result;
        if(query != null && query.trim().equals("changes")) {
            result = placeScheduleService.getSchedulesToBeAddedOrEliminated();
        } else {
            result = new ListResult<>();
            result.setErrorCode(400);
            result.setMessage(new Message("Query inválido"));
        }
        Response response;
        if(result.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(result.getData().get())).build();
        } else {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        }
        return response;
    }
}
