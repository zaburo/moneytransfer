package com.francoismoureau.moneytransfer;

import com.francoismoureau.moneytransfer.model.Account;
import com.francoismoureau.moneytransfer.model.Transfer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Map;

public class MoneyTransferVerticle extends AbstractVerticle {

    private final Map<Integer, Account> accounts = new LinkedHashMap<>();
    private final Map<Integer, Transfer> transfers = new LinkedHashMap<>();

    public static void main(final String[] args) {
        Launcher.executeCommand("run", MoneyTransferVerticle.class.getName());
    }

    @Override
    public void start(Future<Void> fut) {

        createSomeData();

        Router router = Router.router(vertx);

        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>MoneyTransfer</h1>");
        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        router.route().handler(BodyHandler.create());

        router.get("/api/accounts").handler(this::getAllAccounts);
        router.get("/api/accounts/:id").handler(this::getAccount);
        router.post("/api/accounts").handler(this::addAccount);
        router.put("/api/accounts/:id").handler(this::updateAccount);
        router.delete("/api/accounts/:id").handler(this::deleteAccount);

        router.get("/api/transfers").handler(this::getAllTransfers);
        router.get("/api/transfers/:id").handler(this::getTransfer);
        router.post("/api/transfers").handler(this::addTransfer);
        router.put("/api/transfers/:id").handler(this::updateTransfer);

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        8080,
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private void getAllAccounts(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(accounts.values()));
    }

    private void getAccount(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Account account = accounts.get(idAsInteger);
            if (account == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(account));
            }
        }
    }

    private void addAccount(RoutingContext routingContext) {
        try {
            final Account account = Json.decodeValue(routingContext.getBodyAsString(),
                    Account.class);
            accounts.put(account.getId(), account);
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(account));
        } catch (Exception e) {
            routingContext.response().setStatusCode(400).end();
        }
    }

    private void updateAccount(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Account account = accounts.get(idAsInteger);
            if (account == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                boolean updated = false;
                if (json.getString("name") != null && !json.getString("name").isEmpty()) {
                    account.setName(json.getString("name"));
                    updated = true;
                }
                if (json.getString("balance") != null && !json.getString("balance").isEmpty() && (new BigDecimal(json.getString("balance"))).compareTo(BigDecimal.ZERO) >= 0) {
                    account.setBalance(new BigDecimal(json.getString("balance")));
                    updated = true;
                }
                if (json.getString("currency") != null && !json.getString("currency").isEmpty()) {
                    try {
                        account.setCurrency(Currency.getInstance(json.getString("currency")));
                        updated = true;
                    } catch (Exception e) {
                        updated = false;
                    }
                }
                if (!updated) {
                    routingContext.response().setStatusCode(400).end();
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(account));
            }
        }
    }

    private void deleteAccount(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            accounts.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getAllTransfers(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(transfers.values()));
    }

    private void getTransfer(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Transfer transfer = transfers.get(idAsInteger);
            if (transfer == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(transfer));
            }
        }
    }

    private void addTransfer(RoutingContext routingContext) {
        try {
            final Transfer transfer = Json.decodeValue(routingContext.getBodyAsString(),
                    Transfer.class);
            transfers.put(transfer.getId(), transfer);
            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(transfer));
        } catch (Exception e) {
            routingContext.response().setStatusCode(400).end();
        }
    }

    private void updateTransfer(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Transfer transfer = transfers.get(idAsInteger);
            if (transfer == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                if (transfer.getStatus() != Transfer.TransferStatus.EXECUTED && transfer.getStatus() != Transfer.TransferStatus.FAILED && transfer.getAmount().compareTo(BigDecimal.ZERO) > 0 && accounts.get(transfer.getSourceAccountId()).getBalance().compareTo(transfer.getAmount()) > 0 && accounts.get(transfer.getSourceAccountId()).getCurrency().equals(accounts.get(transfer.getDestinationAccountId()).getCurrency()) && accounts.get(transfer.getSourceAccountId()).getCurrency().equals(transfer.getCurrency()) && accounts.get(transfer.getDestinationAccountId()).getCurrency().equals(transfer.getCurrency())) {
                    accounts.get(transfer.getSourceAccountId()).withdraw(transfer.getAmount());
                    accounts.get(transfer.getDestinationAccountId()).deposit(transfer.getAmount());
                    transfer.setStatus(Transfer.TransferStatus.EXECUTED);
                } else {
                    transfer.setStatus(Transfer.TransferStatus.FAILED);
                }
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(transfer));
            }
        }
    }

    private void createSomeData() {
        Account acc1 = new Account("Account 1", new BigDecimal("2345"), Currency.getInstance("EUR"));
        accounts.put(acc1.getId(), acc1);
        Account acc2 = new Account("Account 2", new BigDecimal("437"), Currency.getInstance("EUR"));
        accounts.put(acc2.getId(), acc2);
        Account acc3 = new Account("Account 3", new BigDecimal("10000"), Currency.getInstance("GBP"));
        accounts.put(acc3.getId(), acc3);
    }

}
