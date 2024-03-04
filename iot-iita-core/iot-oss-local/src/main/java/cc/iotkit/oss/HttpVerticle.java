/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.oss;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HttpVerticle extends AbstractVerticle implements Handler<RoutingContext> {
    private HttpServer httpServer;

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().schedule(this::initHttpServer, 3, TimeUnit.SECONDS);
    }

    private void initHttpServer() {
        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create()).handler(this);
        httpServer.requestHandler(router).listen(1121, ar -> {
            if (ar.succeeded()) {
                log.info("http server is listening on port " + ar.result().actualPort());
            } else {
                log.error("Error on starting the server", ar.cause());
            }
        });
    }

    @Override
    public void stop() {
    }

    @Override
    public void handle(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "application/json");
        response.setStatusCode(200);

        try {
            HttpServerRequest request = ctx.request();
            String path = request.path();
            log.info("path:{}", path);

        } catch (Exception e) {
            log.error("消息处理失败", e);
            response.setStatusCode(500);
            end(response);
        }
    }

    private void end(HttpServerResponse response) {
        response.end(new JsonObject()
                .put("code", response.getStatusCode() == 200 ? 0 : response.getStatusCode())
                .toString());
    }

}
