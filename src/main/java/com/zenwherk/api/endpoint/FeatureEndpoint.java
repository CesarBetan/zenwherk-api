package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Feature;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class FeatureEndpoint {

    @Autowired
    private FeatureService featureService;

    @POST
    @Path("/feature")
    public Response insert(Feature feature){
        Result<Feature> featureResult = featureService.insert(feature);
        Response response;
        if(featureResult.getData().isPresent()){
            response = Response.ok(featureResult.getData().get()).build();
        } else {
            switch (featureResult.getErrorCode()){
                case 400:
                    response = Response.status(featureResult.getErrorCode()).entity(featureResult.getMessage()).build();
                    break;
                default:
                    response = Response.serverError().entity(featureResult.getMessage()).build();
                    break;
            }
        }
        return response;
    }

    @DELETE
    @Path("/feature/{uuid}")
    public Response delete(@PathParam("uuid") String uuid) {
        MessageResult result = featureService.delete(uuid);

        Response response;
        if(result.getErrorCode() != null && result.getErrorCode() > 0) {
            switch (result.getErrorCode()) {
                case 400:
                case 500:
                    response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
                    break;
                default:
                    response = Response.serverError().entity(result.getMessage()).build();
            }
        } else {
            response = Response.ok(result.getMessage()).build();
        }

        return response;
    }
}
