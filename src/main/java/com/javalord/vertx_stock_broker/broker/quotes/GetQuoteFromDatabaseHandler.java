package com.javalord.vertx_stock_broker.broker.quotes;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(GetQuoteFromDatabaseHandler.class);
  private final Pool db;

  public GetQuoteFromDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    final String assetParam = context.pathParam("asset");
    LOGGER.info("Asset parameter: {}", assetParam);

    SqlTemplate.forQuery(
      db,
      "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume FROM broker.quote q WHERE asset=#{asset}"
    )
      .mapTo(QuoteEntity.class)
      .execute(Map.of("asset", assetParam))
      .onFailure(error -> {
        LOGGER.info("Asset eeror: {}", error.getLocalizedMessage());
        context.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .end(
            new JsonObject()
              .put("message", "Failed to get quote for asset " + assetParam + " not available!")
              .put("path", context.normalizedPath())
              .toBuffer()
          );
      })
      .onSuccess(quotes -> {
        if (!quotes.iterator().hasNext()) {
          context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(
              new JsonObject()
                .put("message", "Not found to get quote for asset " + assetParam + " not available!")
                .put("path", context.normalizedPath())
                .toBuffer()
            );
          return;
        }
        var response = quotes.iterator().next().toJsonObject();
        context.response().end(response.toBuffer());
      });
  }
}
