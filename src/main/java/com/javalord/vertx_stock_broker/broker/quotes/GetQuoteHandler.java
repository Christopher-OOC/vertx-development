package com.javalord.vertx_stock_broker.broker.quotes;

import com.javalord.vertx_stock_broker.broker.assets.Asset;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class GetQuoteHandler implements Handler<RoutingContext> {

  final Map<String, Quote> cachedQuotes;

  public GetQuoteHandler(final Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  private static final Logger LOGGER =
    LoggerFactory.getLogger(GetQuoteHandler.class);

  @Override
  public void handle(RoutingContext context) {
    final String assetParam = context.pathParam("asset");

    final var maybeQuote = Optional.ofNullable(cachedQuotes.get(assetParam));
    if (maybeQuote.isEmpty()) {
      context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          new JsonObject()
            .put("message", "Quote for asset " + assetParam + " not available!")
            .put("path", context.normalizedPath())
            .toBuffer()
        );
      return;
    }

    final JsonObject response = maybeQuote.get().toJsonObject();

    LOGGER.info(
      "Path {} responds with {}",
      context.normalizedPath(),
      response.encode());
    LOGGER.info("Asset");

    context.response().end(response.toBuffer());
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote
      .builder()
      .asset(new Asset(assetParam))
      .volume(randomValue())
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble());
  }
}
