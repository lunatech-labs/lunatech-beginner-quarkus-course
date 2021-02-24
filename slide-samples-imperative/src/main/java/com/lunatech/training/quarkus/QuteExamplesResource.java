package com.lunatech.training.quarkus;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/qute")
@Produces(MediaType.TEXT_HTML)
public class QuteExamplesResource {

    @Inject
    Template quteExamples;

    @GET
    @Path("/examples")
    public TemplateInstance examples() {

        return quteExamples.data(
                "name", "Erik");

    }

}
