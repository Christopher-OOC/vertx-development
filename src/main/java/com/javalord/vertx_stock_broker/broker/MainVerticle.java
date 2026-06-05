package com.javalord.vertx_stock_broker.broker;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends VerticleBase {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(MainVerticle.class);

  public static final int PORT = 8888;

  public static void main(String[] args) {
    LOGGER.info("Info...");
    System.out.println("Starting...");
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle())
      .onSuccess(id -> {
        LOGGER.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id);
      });
  }

  @Override
  public Future<?> start() {
    return vertx.deployVerticle(
      RestApiVerticle.class.getName(),
      new DeploymentOptions()
        .setInstances(processors())
      )
      .onFailure(failure -> LOGGER.info("Deployment failed"))
      .onSuccess(id -> {
        LOGGER.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
      });
  }

  private int processors() {
    return Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
  }
}
