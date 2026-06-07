package com.javalord.vertx_stock_broker.broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {

  private final Pool db;

  public PutWatchListDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = getAccountId(context);

    var json = context.body().asJsonObject();
    var watchlist = json.mapTo(WatchList.class);

    List<Map<String, Object>> parameterBatch = watchlist.getAssets().stream().map(asset -> {
      Map<String, Object> parameters = Map.of(
        "account_id", accountId,
        "asset", asset.getName()
      );

      return parameters;
    }).toList();

    db.withTransaction(client -> {
      return SqlTemplate.forUpdate(
          client,
          "DELETE FROM broker.watchlist WHERE account_id=#{account_id}"
        ).execute(Map.of("account_id", accountId))
        .onFailure(error -> {
          log.info("Failed to delete into watchlist: {}", error.getLocalizedMessage());
          context.response().setStatusCode(500).end();
        })
        .compose(deleteDone -> {
          return SqlTemplate.forUpdate(
              client,
              "INSERT INTO broker.watchlist VALUES (#{account_id}, #{asset})"
            )
            .executeBatch(parameterBatch)
            .onFailure(error -> {
              log.info("Failed to insert into watchlist: {}", error.getLocalizedMessage());
              context.response().setStatusCode(500).end();
            })
            .onSuccess(result -> {
              context.response().setStatusCode(204).end();
            });
        })
        .onSuccess(result -> {
          context.response().setStatusCode(204).end();
        });
    });
  }

  private static String getAccountId(final RoutingContext context) {
    var accountId = context.pathParam("accountId");
    log.info("{} for account {}", context.normalizedPath(), accountId);

    return accountId;
  }
}
