package com.lunatech.training.quarkus.pricing;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * A simple resource retrieving the in-memory "my-data-stream" and sending the items as server-sent events.
 */
@Path("/prices")
public class PriceResource {
    Logger logger = LoggerFactory.getLogger(PriceResource.class);

    // TODO, can we directly inject a Multi as well?
    // TODO, what does Publisher say about how many consumers must be able to connect?
    @Inject @Channel("prices-in") Publisher<Price> prices;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Publisher<Price> stream() {
        logger.info("Connecting to an SSE consumer!");
        return prices;
    }

    @GET
    @Path("/stream/{productId}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Publisher<Price> stream(@QueryParam("productId") String productId) {
        return Multi.createFrom().publisher(prices).filter(p -> p.productId.equals(productId));
    }

}
