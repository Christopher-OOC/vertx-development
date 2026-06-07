package com.javalord.vertx_stock_broker.broker.config;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class DbConfig {

  String host = "localhost";
  int port = 5432;
  String database = "vertx_stock_broker";
  String user = "postgres";
  String password = "chris";

}
