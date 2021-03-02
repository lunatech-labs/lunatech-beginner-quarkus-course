package com.lunatech.training.quarkus;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductsResource {

    @GET
    public Multi<Product> products() {
        return Product.streamAll();
    }

    @GET
    @Path("{id}")
    public Uni<Product> product(@PathParam("id") Long id) {
        return Product.findById(id);
    }

    @GET
    @Path("search/{term}")
    public Uni<List<Product>> products(@PathParam("term") String term) {
        return Product.find("description like ?1 or name like ?1", "%" + term + "%").list();
    }

}
