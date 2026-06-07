package com.javalord.vertx_stock_broker.broker;

import com.javalord.vertx_stock_broker.broker.assets.AssetsRestApi;
import com.javalord.vertx_stock_broker.broker.config.BrokerConfig;
import com.javalord.vertx_stock_broker.broker.config.ConfigLoader;
import com.javalord.vertx_stock_broker.broker.quotes.QuotesRestApi;
import com.javalord.vertx_stock_broker.broker.watchlist.WatchListRestApi;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(RestApiVerticle.class);

  public static final int PORT = 8888;

  @Override
  public void start(Promise<Void> startPromise) {
    ConfigLoader.load(vertx)
      .onFailure((er) -> LOGGER.info(er.getLocalizedMessage()))
      .onSuccess(configuration -> {
        LOGGER.info("Retrieved configuration: {}", configuration);
        startHttpServerAndAttachRoutes(configuration);
        startPromise.complete();
      });
  }

  private Future<HttpServer> startHttpServerAndAttachRoutes(BrokerConfig configuration) {
    Pool db = createDbPool(vertx, configuration);

    Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());

    AssetsRestApi.attach(restApi, db);
    QuotesRestApi.attach(restApi, db);
    WatchListRestApi.attach(restApi, db);

    return vertx
      .createHttpServer()
      .requestHandler(restApi)
      .listen(configuration.getServerPort())
      .onSuccess(http ->
        LOGGER.info("HTTP server started on port {}", configuration.getServerPort())
      )
      .onFailure(err ->
        LOGGER.error("Failed to start HTTP server", err)
      );
  }

  private static Pool createDbPool(Vertx vertx, BrokerConfig configuration) {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setPort(configuration.getDbConfig().getPort())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(4);

    return Pool.pool(vertx, connectOptions, poolOptions);

  }

  private static Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        return;
      }

      LOGGER.error("Route Error: ", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong").toBuffer());
    };
  }
}
