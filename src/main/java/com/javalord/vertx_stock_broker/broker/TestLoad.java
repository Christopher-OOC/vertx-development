package com.javalord.vertx_stock_broker.broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class TestLoad extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new TestLoad());
  }

  @Override
  public void start() throws Exception {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));

    for (int i = 0; i < 100; i++) {
      System.out.println("getting...");
      System.out.println(client.get("http://localhost:8888/assets").send());
    }
  }
}
