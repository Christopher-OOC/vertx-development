package com.javalord.vertx_stock_broker.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(AssetsRestApi.class);

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "TSLA", "NFLX");


  public static void attach(Router parent) {
    parent.get("/assets").handler(context -> {
      JsonArray response = new JsonArray();
      AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);

      LOGGER.info(
        "Path {} responds with {}",
        context.normalizedPath(),
        response.encode());

      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      context.response()
        .putHeader("Content-Type", "application/json")
        .end(response.toBuffer());
    });
  }
}
