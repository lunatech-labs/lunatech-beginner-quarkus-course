package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Multi;
import org.jboss.resteasy.reactive.RestSseElementType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.LocalDateTime;

@Path("time")
public class TimeResource {

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.TEXT_PLAIN)
    public Multi<String> time() {
        return Multi.createFrom()
                .ticks()
                .every(Duration.ofSeconds(1))
                .map(__ -> LocalDateTime.now().toString());
    }
}
