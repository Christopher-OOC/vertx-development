package com.javalord.vertx_stock_broker.broker.quotes;

import com.javalord.vertx_stock_broker.broker.assets.Asset;
import com.javalord.vertx_stock_broker.broker.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent, Pool db) {
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> {
      cachedQuotes.put(symbol, initRandomQuote(symbol));
    });

    parent
      .get("/quotes/:asset")
      .handler(new GetQuoteHandler(cachedQuotes));
    parent
      .get("/pg/quotes/:asset")
      .handler(new GetQuoteFromDatabaseHandler(db));


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
