package com.javalord.vertx_stock_broker.broker;

import com.javalord.vertx_stock_broker.broker.config.ConfigLoader;
import com.javalord.vertx_stock_broker.broker.db.migration.FlywayMigration;
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
    System.setProperty(ConfigLoader.SERVER_PORT, "12000");
    LOGGER.info("Info...");
    System.out.println("Starting...");
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle())
      .onFailure(er -> LOGGER.info(er.getLocalizedMessage()))
      .compose(next -> migrateDatabase(vertx))
      .onFailure(t -> LOGGER.info("Error occurred while migrating database: {}", t.getLocalizedMessage()))
      .onSuccess(id -> {
        LOGGER.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id);
      });
  }

  private static Future<Void> migrateDatabase(Vertx vertx) {

    ConfigLoader.load(vertx)
      .compose(config -> {
        return FlywayMigration.migrate(vertx, config.getDbConfig());
      });
    return null;
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
