package com.lunatech.training.quarkus;



import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Path("/async")
public class GreetingResource {

    @Inject
    EventBus bus;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public Uni<String> greeting(String name) {
        return bus.<String>request("greeting", name)
                .onItem().transform(Message::body);
    }

    /*@GET
    @Path("event")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    @ConsumeEvent(value = "pricesSend", codec = PriceCodec.class)
    public Multi<Price> priceConsume() {

        return bus.<Price>consumer("prices").toMulti().onItem().transform(Message::body);
    }*/

  /*  @ConsumeEvent(value = "price", codec = PriceCodec.class)
    Uni<Price> priceConsume(Price price) {
        return Uni.createFrom().item(() -> price);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("price")
    public Uni<Price> priceSend() {
        return bus.<Price>request("price", new Price(1L, new BigDecimal(new Random().nextInt()))
                        , new DeliveryOptions().setCodecName(PriceCodec.class.getSimpleName()))
                .onItem().transform(Message::body);
    }
*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("products")
    public Uni<ProductList> sendProducts() {
        return bus.<ProductList>request("products", ProductList.wrap(Product.findAll().list()))

                .onItem().transform(Message::body);
    }

    @ConsumeEvent("products")
    Uni<List<Product>> consumeProduct(List<Product> products) {
        return Uni.createFrom().item(() -> products);
    }
}
