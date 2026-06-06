package com.javalord.vertx_stock_broker.broker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;

  public static BrokerConfig from (final JsonObject config) {
    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);

    if (serverPort == null) {
      throw new RuntimeException("Config port not found at " + serverPort);
    }

    return BrokerConfig.builder().serverPort(config.getInteger(ConfigLoader.SERVER_PORT)).build();
  }

}
