package com.javalord.vertx_stock_broker.broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(DeleteWatchListHandler.class);

  final HashMap<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    String accountId = getAccountId(context);
    final WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));
    context.response().end(deleted.toJsonObject().toBuffer());
  }

  private static String getAccountId(final RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOGGER.info("{} for account {}", context.normalizedPath(), accountId);

    return accountId;
  }
}
