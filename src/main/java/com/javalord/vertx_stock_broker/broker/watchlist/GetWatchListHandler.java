package com.javalord.vertx_stock_broker.broker.watchlist;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class GetWatchListHandler implements Handler<RoutingContext> {

  final HashMap<UUID, WatchList> watchListPerAccount;

  private static final Logger LOGGER =
    LoggerFactory.getLogger(GetWatchListHandler.class);

  public GetWatchListHandler(HashMap<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOGGER.info("{} for account {}", context.normalizedPath(), accountId);
    var watchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
    if (watchList.isEmpty()) {
      context.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          new JsonObject()
            .put("message", "WatchList for account od ID " + accountId + " not available!")
            .put("path", context.normalizedPath())
            .toBuffer()
        );
      return;
    }

    context.response().end(watchList.get().toJsonObject().toBuffer());
  }

  private static String getAccountId(final RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOGGER.info("{} for account {}", context.normalizedPath(), accountId);

    return accountId;
  }
}
