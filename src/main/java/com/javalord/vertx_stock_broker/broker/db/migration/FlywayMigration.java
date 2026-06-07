package com.javalord.vertx_stock_broker.broker.db.migration;

import com.javalord.vertx_stock_broker.broker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(FlywayMigration.class);

  public static Future<Void> migrate(Vertx vertx, DbConfig dbConfig) {

    return vertx.<Void>executeBlocking(() -> {
        excute(dbConfig);
        return null;
      })
      .onSuccess(v ->
        LOGGER.info("Database migration completed successfully"))
      .onFailure(err ->
        LOGGER.error("Database migration failed", err));
  }
  private static void excute(DbConfig dbConfig) {
    String jdbcUrl = String.format(
      "jdbc:postgresql://%s:%d/%s",
      dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getDatabase()
      );

    LOGGER.info("Migrating DB schema using jdbc url: {}", jdbcUrl);

    Flyway flyway = Flyway.configure()
      .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();

    var current = Optional.ofNullable(flyway.info().current());
    current.ifPresent(info -> LOGGER.info("DB schema is at version: {}", info.getVersion()));

    var pendingMigration = flyway.info().pending();
    LOGGER.info("Pending migrations are: {}", printMigrations(pendingMigration));

    flyway.migrate();
  }

  private static String printMigrations(MigrationInfo[] pendingMigration) {
    if (Objects.isNull(pendingMigration)) {
      return "[]";
    }

    return Arrays.stream(pendingMigration)
      .map(each -> each.getVersion() + " - "  + each.getDescription())
      .collect(Collectors.joining(",", "[", "]"));
  }
}
