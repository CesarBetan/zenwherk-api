package com.zenwherk.api.endpoint;


import com.zenwherk.api.domain.Review;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.MessageResult;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class ReviewEndpoint {

    @Autowired
    private ReviewService reviewService;

    @POST
    @Path("/review")
    public Response insert(Review review) {
        Result<Review> reviewResult = reviewService.insert(review);
        Response response;
        if(reviewResult.getData().isPresent()) {
            response = Response.ok(reviewResult.getData().get()).build();
        } else {
            if(reviewResult.getErrorCode() == null || reviewResult.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(reviewResult.getErrorCode()).entity(reviewResult.getMessage()).build();
            }
        }
        return response;
    }

    @POST
    @Path("/review/{uuid}/report")
    public Response report(@PathParam("uuid") String uuid) {
        MessageResult result = reviewService.report(uuid);
        Response response;
        if(result.getErrorCode() == null || result.getErrorCode() < 1) {
            response = Response.ok(result.getMessage()).build();
        }  else {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        }
        return response;
    }

}
