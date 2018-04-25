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

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceEndpoint {

    @Autowired
    private PlaceService placeService;

    @POST
    @Path("/place")
    public Response insert(Place place) {
        Result<Place> placeResult = placeService.insert(place);
        Response response;
        if(placeResult.getData().isPresent()){
            response = Response.ok(placeResult.getData().get()).build();
        } else {
            if(placeResult.getErrorCode() == null || placeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeResult.getErrorCode()).entity(placeResult.getMessage()).build();
            }
        }
        return response;
    }

    @PUT
    @Path("/place/{uuid}")
    public Response update(@PathParam("uuid") String uuid, Place place) {
        Result<Place> placeResult = placeService.update(uuid, place);
        Response response;
        if (placeResult.getData().isPresent()) {
            response = Response.ok(placeResult.getData().get()).build();
        } else {
            if(placeResult.getErrorCode() == null || placeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeResult.getErrorCode()).entity(placeResult.getMessage()).build();
            }
        }
        return response;
    }

    @PUT
    @Path("/place/{uuid}/approval")
    public Response approve(@PathParam("uuid") String uuid) {
        Result<Place> placeResult = placeService.approveOrReject(uuid, true);
        Response response;
        if (placeResult.getData().isPresent()) {
            response = Response.ok(placeResult.getData().get()).build();
        } else {
            if(placeResult.getErrorCode() == null || placeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeResult.getErrorCode()).entity(placeResult.getMessage()).build();
            }
        }
        return response;
    }

    @PUT
    @Path("/place/{uuid}/rejection")
    public Response reject(@PathParam("uuid") String uuid) {
        Result<Place> placeResult = placeService.approveOrReject(uuid, false);
        Response response;
        if (placeResult.getData().isPresent()) {
            response = Response.ok(placeResult.getData().get()).build();
        } else {
            if(placeResult.getErrorCode() == null || placeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeResult.getErrorCode()).entity(placeResult.getMessage()).build();
            }
        }
        return response;
    }

    @DELETE
    @Path("/place/{uuid}")
    public Response delete(@PathParam("uuid") String uuid) {
        Result<Place> placeResult = placeService.delete(uuid);
        Response response;
        if (placeResult.getData().isPresent()) {
            response = Response.ok(placeResult.getData().get()).build();
        } else {
            if(placeResult.getErrorCode() == null || placeResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(placeResult.getErrorCode()).entity(placeResult.getMessage()).build();
            }
        }
        return response;
    }

    @GET
    @Path("/place_proposals")
    public Response searchToBeApproved() {
        ListResult<Place> result = placeService.getPlacesToBeAdded();
        Response response;
        if(result.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(result.getData().get())).build();
        } else {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        }
        return response;
    }
}
