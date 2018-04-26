package com.zenwherk.api.endpoint;

import com.zenwherk.api.domain.Picture;
import com.zenwherk.api.pojo.Message;
import com.zenwherk.api.pojo.Result;
import com.zenwherk.api.service.PictureService;
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
public class PictureEndpoint {

    @Autowired
    private PictureService pictureService;

    @POST
    @Path("/picture")
    public Response insert(Picture picture) {
        Result<Picture> result = pictureService.insert(picture);
        Response response;
        if(result.getData().isPresent()) {
            response = Response.ok(result.getData().get()).build();
        } else {
            if(result.getErrorCode() == null || result.getErrorCode() < 1) {
                response = Response.serverError().entity(new Message("Error de servidor")).build();
            }  else {
                response = Response.status(result.getErrorCode()).entity(result.getMessage()).build();
            }
        }
        return response;
    }

}
