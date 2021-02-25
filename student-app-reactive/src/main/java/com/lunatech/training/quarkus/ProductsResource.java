package com.lunatech.training.quarkus;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductsResource {

    @Inject
    PgPool client;

    @GET
    public Multi<Product> allProducts() {
        return client
          .query("SELECT name, description, price FROM products ORDER BY name ASC")
          .execute()
          .toMulti()
          .flatMap(rows -> Multi.createFrom().iterable(rows))
          .map(Product::from);
    }

    @Path("{productId}")
    @GET
    // TODO, should we do this or Uni<Optional<Product>> ?
    public Uni<Product> productDetails(@PathParam("productId") Long productId) {
        return client
                .preparedQuery("SELECT name, description, price FROM products WHERE id = $1")
                .execute(Tuple.of(productId))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Product.from(iterator.next()) : null);

    }

    @Path("search")
    @GET
    public Multi<Product> productDetails(
            @QueryParam("query") String query) {
        return client
                .preparedQuery("SELECT name, description, price FROM products WHERE name ILIKE %$1% or description ILIKE %$1%")
                .execute()
                .toMulti()
                .flatMap(rows -> Multi.createFrom().iterable(rows))
                .map(Product::from);
    }

}
