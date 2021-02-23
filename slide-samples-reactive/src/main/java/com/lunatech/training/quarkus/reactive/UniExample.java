package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.UniSubscriber;
import io.smallrye.mutiny.subscription.UniSubscription;

public class UniExample {
    public static void main(String... args) throws InterruptedException {
        Uni<Integer> myUni = Uni.createFrom().item(() -> {
            System.out.println("Creating the item!");
            return 5;
        });

        // Nothing has been printed at this point

        System.out.println("Subscribing:");
        myUni.subscribe().with(System.out::println); // This prints 'Creating the item!' and '5'

        System.out.println("Subscribing again:");
        myUni.subscribe().with(System.out::println);

    }
}
