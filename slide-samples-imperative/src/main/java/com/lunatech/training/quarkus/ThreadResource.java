package com.lunatech.training.quarkus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/threads")
public class ThreadResource {

    @GET
    @Path("/slow")
    public String slow() throws InterruptedException {
        String thread = Thread.currentThread().getName();
        System.out.println("Thread: " + thread);
        Thread.sleep(1000);
        return thread;
    }

}
