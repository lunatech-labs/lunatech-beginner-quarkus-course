package com.lunatech.training.quarkus.reactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
public class HelloResource {

    @Inject
    ObjectMapper mapper;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello!";
    }

    @GET
    @Path("/uni")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> helloUni() {
        return Uni.createFrom().item("Hello!");
    }

    @GET
    @Path("/multi")
    @Produces(MediaType.TEXT_PLAIN)
    public Multi<String> helloMulti() {
        return Multi.createFrom().items("Hello", "world!");
    }

    @GET
    @Path("/multijson")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> helloMultiJSON() throws JsonProcessingException {
        return Multi.createFrom().items(
                mapper.writeValueAsString("Hello"),
                mapper.writeValueAsString("world!"));
    }

    @GET
    @Path("methods")
    public Multi<String> multiMethods() {
        return Multi.createFrom().items("One", "Two", "Three", "Four", "Five", "Six")
                .map(String::toUpperCase)
                .filter(s -> s.length() >= 4)
                .flatMap(s -> Multi.createFrom().items(s.toCharArray()))
                .map(String::valueOf);
    }
}
