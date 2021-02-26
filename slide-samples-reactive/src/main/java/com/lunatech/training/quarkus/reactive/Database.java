package com.lunatech.training.quarkus.reactive;

import com.fasterxml.jackson.databind.JsonNode;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgConnection;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vertx.pgclient.PgNotification;
import org.jboss.resteasy.reactive.RestSseElementType;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/db")
public class Database {

    @Inject
    PgPool client;

    public void q() {

        Multi<Person> people = client.query("SELECT name, age FROM people").execute()
               .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(Person::fromRow);




    }

    static class Person {
        public Person(String name, Integer age){}

        static Person fromRow(Row row) {
            return new Person(row.getString("nam"), row.getInteger("age"));
        }
    }

    @Path("/listen/{channel}")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Multi<JsonObject> listen(@PathParam("channel") String channel) {
        return client.getConnection()
            .onItem().transformToMulti(connection -> {
                Multi<PgNotification> notifications = Multi.createFrom().
                        emitter(c -> toPgConnection(connection).notificationHandler(c::emit));
                return connection.query("LISTEN " + channel).execute().onItem().transformToMulti(__ -> notifications);
            })
            .map(PgNotification::toJson);
    }

    @Path("/notify/{channel}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.WILDCARD)
    public Uni<String> notif(@PathParam("channel") String channel, String stuff) {
        return client.preparedQuery("NOTIFY " + channel +  ", $$" + stuff + "$$").execute()
                .map(rs -> "Posted to " + channel + " channel");
    }

    // We have to do some type juggling here. Solved in the mutiny client v2.
    PgConnection toPgConnection(SqlConnection sqlConnection) {
        return new PgConnection((io.vertx.pgclient.PgConnection) sqlConnection.getDelegate());
    }
}
