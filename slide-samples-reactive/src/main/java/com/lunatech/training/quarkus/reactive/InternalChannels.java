package com.lunatech.training.quarkus.reactive;

import com.github.javafaker.Faker;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.time.Duration;

public class InternalChannels {

    private static final Faker faker = new Faker();

    /*
    @Outgoing("greet-subjects")
    public Multi<String> greetSubjectsProducer() {
        return Multi.createFrom()
                .ticks()
                .every(Duration.ofSeconds(1))
                .map(__ -> faker.name().fullName())
                .onOverflow().buffer(1);
    }

    @Incoming("greet-subjects")
    @Outgoing("greets-out")
    public Greet greetSubjectsConsumer(String subject) {
        System.out.println("Created a greet!");
        return new Greet(subject, "Helloooo");
    }


    private int counter = 0;
    @Incoming("greets-in")
    public void greetPrinter(Greet greet) {
        if(++counter % 3 == 0) {
            throw new RuntimeException("Crashing on message for " + greet.subject);
        }

        System.out.println(greet.greet + " " + greet.subject);
    }
*/



/**

    @Outgoing("greet-subjects")
    public Multi<String> greetSubjectsProducer() {
        return Multi.createFrom()
                .ticks()
                .every(Duration.ofSeconds(1))
                .map(__ -> faker.name().fullName())
                .onOverflow().buffer(1);
    }



    @Incoming("greet-subjects")
    @Outgoing("greets")
    public Multi<String> greetSubjectsConsumer(String subject) {
        return Multi.createFrom().items(
                "Hey " + subject + "!",
                "Ho " + subject + "!");
    }

    @Incoming("greets")
    public CompletableFuture<Message<?>> greetPrinter(Message<String> greet) {
        System.out.println(greet.getPayload());
        return CompletableFuture.completedFuture(greet);

    }
**/
}
