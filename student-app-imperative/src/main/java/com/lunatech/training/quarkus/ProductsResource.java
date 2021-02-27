package com.lunatech.training.quarkus;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/products")
public class ProductsResource {

    @Inject
    private MeterRegistry registry;

    @GET
    public List<Product> allProducts() {
        return Product.listAll(Sort.ascending("name"));
    }

    @Transactional
    @POST
    public Response add(Product product) {
        Product.persist(product);
        return Response.created(URI.create("/products/" + product.id)).build();
    }

    @Path("{productId}")
    @GET
    public Product productDetails(@PathParam("productId") Long productId) {
        return Product.<Product>findByIdOptional(productId).orElseThrow(() -> new NotFoundException("Product not found."));
    }

    @Path("search")
    @GET
    public List<Product> productDetails(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("0") Integer page) {
        return registry.timer("product_search_duration_seconds").record(() ->
            Product.search(query, page, 3));
    }




}
