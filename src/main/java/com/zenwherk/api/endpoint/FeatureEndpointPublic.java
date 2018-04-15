package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Feature;
import com.zenwherk.api.pojo.ListResponse;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Component
@Path("/v1/public")
@Produces(MediaType.APPLICATION_JSON)
public class FeatureEndpointPublic {

    @Autowired
    private FeatureService featureService;

    @GET
    @Path("/feature/{uuid}")
    public Response getFeatureByUuid(@PathParam("uuid") String uuid) {
        Result<Feature> featureResult = featureService.getFeatureByUuid(uuid, false);
        Response response;
        if(featureResult.getData().isPresent()) {
            response = Response.ok(featureResult.getData().get()).build();
        } else {
            response = Response.status(featureResult.getErrorCode()).entity(featureResult.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/feature")
    public Response searchFeatures(@QueryParam("q") String query) {
        ListResult<Feature> featureListResult = featureService.searchFeatures(query, false);
        Response response;
        if(featureListResult.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(featureListResult.getData().get())).build();
        } else {
            response = Response.status(featureListResult.getErrorCode()).entity(featureListResult.getMessage()).build();
        }
        return response;
    }
}
