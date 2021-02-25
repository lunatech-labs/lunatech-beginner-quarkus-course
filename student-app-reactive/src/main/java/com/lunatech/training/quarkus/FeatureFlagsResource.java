package com.lunatech.training.quarkus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/feature-flags")
public class FeatureFlagsResource {

    @Inject
    ObjectMapper mapper;

    @GET
    public ObjectNode flags() {
        ObjectNode node = mapper.createObjectNode();
        node.put("productDetails", true);
        node.put("productSearch", true);
        node.put("reactivePrices", true);
        return node;
    }


}
