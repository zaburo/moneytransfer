package com.francoismoureau.moneytransfer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class MoneyTransferVerticleTest {

    private Vertx vertx;
    private Integer port;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );
        vertx.deployVerticle(MoneyTransferVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testVertex(TestContext context) {
        final Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/", response -> response.handler(body -> {
            context.assertTrue(body.toString().contains("MoneyTransfer"));
            async.complete();
        }));
    }

    @Test
    public void testIndexPageServed(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/assets/index.html", response -> {
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"), "text/html;charset=UTF-8");
            response.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("<title>MoneyTransfer</title>"));
                async.complete();
            });
        });
    }
}
