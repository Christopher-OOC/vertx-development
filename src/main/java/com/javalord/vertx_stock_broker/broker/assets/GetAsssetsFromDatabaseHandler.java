package com.javalord.vertx_stock_broker.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAsssetsFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(GetAsssetsFromDatabaseHandler.class);

  private final SqlClient db;

  public GetAsssetsFromDatabaseHandler(SqlClient db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    db.query("SELECT a.value FROM broker.assets a")
      .execute()
      .onFailure(error -> {
        LOGGER.info("Failure: {}", error.getLocalizedMessage());
        context.response()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(
            new JsonObject()
              .put("", "Could not get data from db!")
              .put("path", context.normalizedPath())
              .toBuffer()
          );
        return;
      })
      .onSuccess(result -> {
        var response = new JsonArray();
        result.forEach(row -> {
          response.add(row.getValue("value"));
        });

        LOGGER.info(
          "Path {} responds with {}",
          context.normalizedPath(),
          response.encode());

        context.response()
          .putHeader("Content-Type", "application/json")
          .end(response.toBuffer());
      });
  }
}
