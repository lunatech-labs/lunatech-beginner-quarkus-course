package com.lunatech.training.quarkus.pricing;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.resteasy.annotations.SseElementType;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A simple resource retrieving the in-memory "my-data-stream" and sending the items as server-sent events.
 */
@Path("/prices")
public class PriceResource {
    Logger logger = LoggerFactory.getLogger(PriceResource.class);

    // TODO, learn mor about 'ack'-ing. Doing it per message ain't great.
    private final Multi<Price> broadcaster;

    @Inject
    public PriceResource(@Channel("prices-in") Multi<KafkaRecord<Object, Price>> prices) {
        broadcaster = prices.invoke(Message::ack).map(Message::getPayload).broadcast().toAllSubscribers();
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Publisher<Price> stream() {
        logger.info("Connecting to an SSE consumer!");
        return broadcaster;
    }


    @GET
    @Path("/stream/{productId}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Publisher<Price> stream(@PathParam("productId") Long productId) {
        return broadcaster.filter(p -> p.productId.equals(productId));
    }

}
