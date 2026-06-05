package com.javalord.vertx_stock_broker.assets;

import com.javalord.vertx_stock_broker.broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(TestQuotesRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/assets")
      .send()
      .onComplete(testContext.succeeding(response -> {
        JsonArray jsonArray = response.bodyAsJsonArray();
        LOGGER.info("Response: {}", jsonArray);

        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }
}
