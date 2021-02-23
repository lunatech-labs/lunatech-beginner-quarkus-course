package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Multi;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VisualEvents4 {

    // Cancellation
    public static void main(String... args) {
        Stream<Integer> out = Multi.createFrom().items(1,2,3,4,5,6)
                .onSubscribe().invoke(() -> System.out.println("⬇️ Subscribed"))
                .onItem().invoke(i -> System.out.println("⬇️ Received item: " + i))
                .onFailure().invoke(f -> System.out.println("⬇️ Failed with " + f))
                .onCompletion().invoke(() -> System.out.println("⬇️ Completed"))
                .onCancellation().invoke(() -> System.out.println("⬆️ Cancelled"))
                .onRequest().invoke(l -> System.out.println("⬆️ Requested: " + l))
                // Use a buffer capacity of 2
                .subscribe().asStream(2, () -> new ArrayBlockingQueue<>(2));

        Set<Integer> set = out.limit(2).collect(Collectors.toSet());
        out.close();

//         System.out.println(set);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
