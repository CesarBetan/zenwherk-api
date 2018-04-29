package com.zenwherk.api.config;


import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.zenwherk.api.endpoint.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(JacksonJaxbJsonProvider.class);
        registerEndpoints();
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }

    public void registerEndpoints() {
        register(FavoriteEndpoint.class);
        register(PictureEndpoint.class);
        register(PlaceChangeEndpoint.class);
        register(PlaceEndpoint.class);
        register(PlaceEndpointPublic.class);
        register(PlaceFeatureChangeEndpoint.class);
        register(PlaceFeatureEndpoint.class);
        register(PlaceFeatureEndpointPublic.class);
        register(PlaceScheduleChangeEndpoint.class);
        register(PlaceScheduleEndpoint.class);
        register(PlaceScheduleEndpointPublic.class);
        register(ReviewEndpoint.class);
        register(ReviewEndpointPublic.class);
        register(StatsEndpoint.class);
        register(UserEndpoint.class);
        register(UserEndpointPublic.class);
    }
}
