package com.javalord.vertx_stock_broker.broker.watchlist;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(final Router parent) {
    final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
    final String path = "/account/watchlist/:accountId";

    parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));
    parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));
    parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));
  }

  private static String getAccountId(final RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOGGER.info("{} for account {}", context.normalizedPath(), accountId);

    return accountId;
  }
}
