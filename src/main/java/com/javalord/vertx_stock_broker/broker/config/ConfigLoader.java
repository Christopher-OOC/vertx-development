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

  public static String SERVER_PORT = "SERVER_PORT";
  public static String DB_HOST = "DB_HOST";
  public static String DB_PORT = "DB_PORT";
  public static String DB_DATABASE = "DB_DATABASE";
  public static String DB_USER = "DB_USER";
  public static String DB_PASSWORD = "DB_PASSWORD";
  public static String CONFIG_FILE =  "application.yml";

  public static final List<String> EXPOSED_ENVIRONMENTS_VARIABLES =
    Arrays.asList(SERVER_PORT, DB_HOST, DB_PORT, DB_DATABASE, DB_USER, DB_PASSWORD);

  public static Future<BrokerConfig> load(Vertx vertx) {

    final var exposedKeys = new JsonArray();
    EXPOSED_ENVIRONMENTS_VARIABLES.forEach(exposedKeys::add);

    var envStore = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));

    var propertyStore = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("cache", false));

    var yamlStore = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", CONFIG_FILE));

    var retriever = ConfigRetriever.create(
      vertx,
      new ConfigRetrieverOptions()
        .addStore(envStore)
        .addStore(propertyStore)
        .addStore(yamlStore)
    );

    return retriever.getConfig().map(BrokerConfig::from);
  }
}
