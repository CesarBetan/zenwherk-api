package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Stats;
import com.zenwherk.api.pojo.ListResponse;
import com.zenwherk.api.pojo.ListResult;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class StatsEndpoint {

    @Autowired
    private StatsService statsService;

    @GET
    @Path("/stats")
    public Response getNewUsers() {
        ListResult<Stats> result = statsService.getNewUsersLastWeek();
        Response response;
        if(result.getData().isPresent()) {
            response = Response.ok(new ListResponse<>(result.getData().get())).build();
        } else {
            response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
        }
        return response;
    }
}
