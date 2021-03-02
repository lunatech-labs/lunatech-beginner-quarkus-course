package com.lunatech.training.quarkus.pricing;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestSseElementType;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/prices")
public class PriceResource {
    Logger logger = LoggerFactory.getLogger(PriceResource.class);

    @Channel("prices-in")
    Multi<Price> prices;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Multi<Price> stream() {
        logger.info("SSE consumer connected to /prices/stream");
        return prices;
    }

    @GET
    @Path("/stream/{productId}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Multi<Price> stream(@PathParam("productId") Long productId) {
        logger.info("SSE consumer connected to /prices/stream/" + productId);
        return prices.filter(p -> p.productId.equals(productId));
    }

    @GET
    @Path("foo")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.TEXT_PLAIN)
    public Multi<String> foo() {
        return Multi.createFrom().items("foo", "bar", "quux");
    }

}
