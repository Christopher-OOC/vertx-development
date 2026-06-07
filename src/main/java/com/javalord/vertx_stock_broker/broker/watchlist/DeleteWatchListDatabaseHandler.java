package com.javalord.vertx_stock_broker.broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DeleteWatchListDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  public DeleteWatchListDatabaseHandler(Pool db) {
      this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = getAccountId(context);

    SqlTemplate.forUpdate(
      db,
      "DELETE FROM broker.watchlist WHERE account_id=#{account_id}"
    ).execute(Map.of("account_id", accountId))
      .onFailure(error -> {
        log.info("Failed to delete into watchlist: {}", error.getLocalizedMessage());
        context.response().setStatusCode(500).end();
      })
      .onSuccess(result -> {
        context.response().setStatusCode(204).end();
      });
  }

  private static String getAccountId(final RoutingContext context) {
    var accountId = context.pathParam("accountId");
    log.info("{} for account {}", context.normalizedPath(), accountId);

    return accountId;
  }
}
