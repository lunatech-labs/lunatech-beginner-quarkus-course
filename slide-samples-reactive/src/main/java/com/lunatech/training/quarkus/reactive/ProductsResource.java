package com.lunatech.training.quarkus.reactive;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.function.Function;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductsResource {


    @Inject
    Mutiny.SessionFactory sessionFactory;

    @GET
    @Path("{id}")
    public Uni<Tuple2<Product, Set<Identifier>>> getById(@PathParam("id") Long id) {

        return sessionFactory.withSession(session ->
            session.find(Product.class, id).chain(product ->
                    Mutiny.fetch(product.identifiers)
                            .map(identifiers -> Tuple2.of(product, identifiers))));



    }

}
