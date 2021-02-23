package com.lunatech.training.quarkus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("json-object")
public class JsonObjectResource {

    @Inject
    ObjectMapper mapper;

    @GET
    public ObjectNode node() {
        ObjectNode node = mapper.createObjectNode();
        node.put("greeting", "Hello");
        node.put("subject", "Quarkus Students");
        return node;
    }
}
