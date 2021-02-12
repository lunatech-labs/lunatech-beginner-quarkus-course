package com.lunatech.training.quarkus;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/catalogue")
@Produces(MediaType.TEXT_HTML)
public class ProductsQuteResource {

    @Inject
    Template catalogue;

    @ResourcePath("product-details.html")
    Template productDetails;

    @GET
    public TemplateInstance catalogue() {
        List<Product> products = Product.listAll();
        return catalogue.data("products", products);
    }

    @GET
    @Path("{productId}")
    public TemplateInstance product(@PathParam("productId") long productId) {
        return Product.findByIdOptional(productId)
                .map(product -> productDetails.data("product", product))
                .orElseThrow(() -> new NotFoundException("Product not found!"));
    }

}
