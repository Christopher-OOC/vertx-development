package com.javalord.vertx_stock_broker.broker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;

public class ConfigLoader {

  static String SERVER_PORT = "SERVER_PORT";
  static final List<String> EXPOSED_ENVIRONMENTS_VARIABLES = Arrays.asList(SERVER_PORT);

  public static Future<BrokerConfig> load(Vertx vertx) {

    final var exposedKeys = new JsonArray();
    EXPOSED_ENVIRONMENTS_VARIABLES.forEach(exposedKeys::add);

    var envStore = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));

    var retriever = ConfigRetriever.create(
      vertx,
      new ConfigRetrieverOptions()
        .addStore(envStore)
    );

    return retriever.getConfig().map(BrokerConfig::from);
  }
}
