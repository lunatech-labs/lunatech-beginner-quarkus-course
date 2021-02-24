package com.lunatech.training.quarkus.reactive;

import io.smallrye.common.annotation.Blocking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

}
