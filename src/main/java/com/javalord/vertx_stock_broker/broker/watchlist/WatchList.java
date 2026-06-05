package com.javalord.vertx_stock_broker.broker.watchlist;

import com.javalord.vertx_stock_broker.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {

  List<Asset> assets;

  JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
