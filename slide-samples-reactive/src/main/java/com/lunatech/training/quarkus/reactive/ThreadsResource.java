package com.lunatech.training.quarkus.reactive;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("threads")
@Produces(MediaType.TEXT_PLAIN)
public class ThreadsResource {

    @GET
    @Path("/regular")
    public String regular() {
        return Thread.currentThread().getName();
    }

    @GET
    @Path("/regular-slow")
    public String regularSlow() throws InterruptedException {
        Thread.sleep(1000);
        return Thread.currentThread().getName();
    }

    @GET
    @Blocking
    @Path("/blocking-slow")
    public String blockingSlow() throws InterruptedException {
        Thread.sleep(1000);
        return Thread.currentThread().getName();
    }

    @GET
    @Path("/nonblocking-slow")
    public Uni<String> nonblockingSlow() {
        return Uni.createFrom().item(Thread.currentThread().getName())
                .onItem().delayIt().by(Duration.ofSeconds(1))
                .onItem().transform(i ->
                        "Initial: " + i + ", later: " + Thread.currentThread().getName());
    }
}
