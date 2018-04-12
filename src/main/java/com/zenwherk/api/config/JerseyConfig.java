package com.zenwherk.api.config;


import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(JacksonJaxbJsonProvider.class);
        packages("com.zenwherk.api.endpoint");
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }
}
