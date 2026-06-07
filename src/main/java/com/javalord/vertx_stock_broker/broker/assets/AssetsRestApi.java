package com.javalord.vertx_stock_broker.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(AssetsRestApi.class);

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "TSLA", "NFLX");


  public static void attach(Router parent, SqlClient db) {
    parent.get("/assets").handler(new GetAssetsHandler());
    parent.get("/pg/assets").handler(new GetAsssetsFromDatabaseHandler(db));
  }
}
