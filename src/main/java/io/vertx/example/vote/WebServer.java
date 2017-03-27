package io.vertx.example.vote;


import io.vertx.core.*;
import io.vertx.core.http.HttpHeaders;
import io.vertx.example.utils.MultipleFutures;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.RedisClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaumeUnice on 24/03/17.
 */
public class WebServer extends AbstractVerticle {

    public static final int REDIS_PORT = 8889;

    private List<String> deploymentIds;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        deploymentIds = new ArrayList<>(3);
    }

    @Override
    public void start(Future<Void> future) {


        MultipleFutures dbDeployments = new MultipleFutures();
        dbDeployments.add(this::deployRedis);
        dbDeployments.add(this::deployServer);
        dbDeployments.setHandler(result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
//                whatsNext.handle(future);
                future.complete();
            }
        });
        dbDeployments.start();



    }

    private void deployServer(Future<Void> future) {
//        vertx
//                .createHttpServer()
//                .requestHandler(r -> {
//                    vertx.eventBus().publish("lol", "test");
//                    r.response().end("<h1>Hello from my first " +
//                            "Vert.x 3 application</h1>");
//                })
//                .listen(8080, result -> {
//                    if (result.succeeded()) {
//                        future.complete();
//                    } else {
//                        future.fail(result.cause());
//                    }
//                });

        final Router router = Router.router(vertx);

//        router.post("/")
//                .handler(req -> req.response()
//                        .putHeader("content-type", "text/html")
//                        .end("<html><body><h1>Hello World</h1></body></html>"));

        router.post("/")
                .handler(this::vote);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
        try {
            System.out.println(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void vote (RoutingContext context) {
        RedisClient redis = RedisClient.create(Vertx.vertx());
        String test = "{'voter_id': 2, 'vote': 'cat')";
        redis.rpush("vote", test, r -> {
            if (r.succeeded()) {
                System.out.println("key stored");
            } else {
                System.out.println("Connection or Operation Failed " + r.cause());
            }
        });
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end("{}");
    }
    private void deployRedis(Future<Void> future) {
        DeploymentOptions options = new DeploymentOptions();
        options.setWorker(true);
        vertx.deployVerticle(RedisVerticle.class.getName(), options, result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                deploymentIds.add(result.result());
                future.complete();
            }
        });
    }
}