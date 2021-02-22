package com.lunatech.training.quarkus.pricing;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class MinimumPriceTransformer {
    Logger logger = LoggerFactory.getLogger(MinimumPriceTransformer.class);

    private static final BigDecimal MINIMUM_PRICE = new BigDecimal(30);

    @Incoming("raw-prices-in")
    @Outgoing("prices-out")
    @Broadcast // TODO, explain this
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING) // TODO, explain this in the slides
    public Price process(Price price) {
        // TODO, make this crash occasionally, to demonstrate the dead-letter topic.
        if(price.price.compareTo(MINIMUM_PRICE) > 0) {
            return price;
        } else {
            return new Price(price.productId, price.price.add(MINIMUM_PRICE));
        }
    }

}
