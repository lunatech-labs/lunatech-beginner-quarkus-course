package com.lunatech.training.quarkus;

import io.quarkus.panache.common.Sort;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/products")
public class ProductsResource {

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
        return Product.search(query, page, 3);
    }




}
