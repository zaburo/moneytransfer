package com.francoismoureau.moneytransfer;

import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class MoneyTransferIT {

    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }

    @Test
    public void testRetrieveAllAccounts() {
        final int id = get("/api/accounts").then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("find { it.balance==2345 }.id");
        get("/api/accounts/" + id).then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("John Doe"))
                .body("currency", equalTo("EUR"));
    }

    @Test
    public void testRetrieveOneAccountPass() {
        get("/api/accounts/0").then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(0))
                .body("name", equalTo("John Doe"))
                .body("currency", equalTo("EUR"));
    }

    @Test
    public void testRetrieveOneAccountFail() {
        get("/api/accounts/999").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testAddAccountPass() {
        given().body("{\n" +
                "    \"name\": \"Kate\",\n" +
                "    \"balance\": \"50000\",\n" +
                "    \"currency\": \"GBP\"\n" +
                "}")
                .when()
                .post("api/accounts")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    public void testAddAccountFail() {
        given().body("{\n" +
                "    \"name\": \"Kate\",\n" +
                "    \"balance\": \"50000\",\n" +
                "    \"currency\": \"djskjdsk\"\n" +
                "}")
                .when()
                .post("api/accounts")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testUpdateAccountPass() {
        given().body("{\n" +
                "    \"balance\": \"50000\",\n" +
                "    \"currency\": \"EUR\"\n" +
                "}")
                .when()
                .put("api/accounts/0")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(0))
                .body("name", equalTo("John Doe"))
                .body("balance", equalTo(50000))
                .body("currency", equalTo("EUR"));
    }

    @Test
    public void testUpdateAccountFail() {
        given().body("{\n" +
                "    \"currency\": \"Eskdskdj\"\n" +
                "}")
                .when()
                .put("api/accounts/0")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testDeleteOneAccountPass() {
        delete("/api/accounts/0").then()
                .assertThat()
                .statusCode(204);
        get("/api/accounts/0").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testDeleteOneAccountFail() {
        delete("/api/accounts/999").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testRetrieveAllTransfers() {
        final int id = get("/api/transfers").then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("find { it.amount==650 }.id");
        get("/api/transfers/" + id).then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("sourceAccountId", equalTo(0))
                .body("destinationAccountId", equalTo(1))
                .body("amount", equalTo(650))
                .body("currency", equalTo("EUR"))
                .body("comment", equalTo("Rent"));
    }

    @Test
    public void testRetrieveOneTransferPass() {
        get("/api/transfers/0").then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(0))
                .body("sourceAccountId", equalTo(0))
                .body("destinationAccountId", equalTo(1))
                .body("amount", equalTo(650))
                .body("currency", equalTo("EUR"))
                .body("comment", equalTo("Rent"));
    }

    @Test
    public void testRetrieveOneTransferFail() {
        get("/api/transfers/999").then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testAddTransferPass() {
        given().body("{\n" +
                "    \"sourceAccountId\": \"1\",\n" +
                "    \"destinationAccountId\": \"0\",\n" +
                "    \"amount\": \"1000\",\n" +
                "    \"currency\": \"USD\",\n" +
                "    \"comment\": \"test transfer\"\n" +
                "}")
                .when()
                .post("api/transfers")
                .then()
                .assertThat()
                .statusCode(201);
    }

    @Test
    public void testAddTransferFail() {
        given().body("{\n" +
                "    \"sourceAccountId\": \"1\",\n" +
                "    \"destinationAccountId\": \"0\",\n" +
                "    \"amount\": \"1000\",\n" +
                "    \"currency\": \"UkfjSD\",\n" +
                "    \"comment\": \"test transfer\"\n" +
                "}")
                .when()
                .post("api/transfers")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testUpdateTransferPass() {
        put("api/transfers/0")
                .then()
                .assertThat()
                .body("status", equalTo("EXECUTED"));
    }

    @Test
    public void testUpdateTransferFail() {
        put("api/transfers/1")
                .then()
                .assertThat()
                .body("status", equalTo("FAILED"));
    }

}
