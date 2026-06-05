package com.javalord.vertx_stock_broker.broker.assets;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER =
    LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext context) {
      JsonArray response = new JsonArray();
      AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);

      LOGGER.info(
        "Path {} responds with {}",
        context.normalizedPath(),
        response.encode());

      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer());
    }
}
