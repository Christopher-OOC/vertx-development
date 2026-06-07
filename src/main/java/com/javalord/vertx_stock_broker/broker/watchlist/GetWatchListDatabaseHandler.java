package com.javalord.vertx_stock_broker.broker.watchlist;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GetWatchListDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  public GetWatchListDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = context.pathParam("accountId");

    SqlTemplate.forQuery(
      db,
      "SELECT w.asset FROM broker.watchlist w WHERE w.account_id=#{account_id}"
      )
      .mapTo(Row::toJson)
      .execute(Map.of("account_id", accountId))
      .onFailure(error -> {
        log.info("Error occurs trying to get watchlist: {}", error.getLocalizedMessage());
        context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(
            new JsonObject()
              .put("message", "WatchList for account od ID " + accountId + " not available!")
              .put("path", context.normalizedPath())
              .toBuffer()
          );
      })
      .onSuccess(assets -> {
       if (!assets.iterator().hasNext()) {
         context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
           .end(
             new JsonObject()
               .put("message", "WatchList for account od ID " + accountId + " not available!")
               .put("path", context.normalizedPath())
               .toBuffer()
           );
       }

       var response = new JsonArray();
       assets.forEach(response::add);
        context.response().end(response.toBuffer());
      });
  }
}
