package com.francoismoureau.moneytransfer;

import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.get;
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
                .jsonPath().getInt("find { it.name=='Account 1' }.id");
        get("/api/accounts/" + id).then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Account 1"))
                .body("balance", equalTo(2345))
                .body("currency", equalTo("EUR"));
    }

//    @Test
//    public void testRetrieveOneAccount() {
//        final int id = get("/api/accounts/1").then()
//                .assertThat()
//                .statusCode(200)
//                .extract()
//                .jsonPath().getInt("find { it.name=='Account 1' }.id");
//        get("/api/accounts/" + id).then()
//                .assertThat()
//                .statusCode(200)
//                .body("id", equalTo(id))
//                .body("name", equalTo("Account 1"))
//                .body("balance", equalTo(2345))
//                .body("currency", equalTo("EUR"));
//    }

    //@Test
    //public void testAddAccount()

//    @Test
//    public void testAddAndDeleteAccount() {
//        Account account = given()
//                .body("{\"name\":\"Jameson\", \"origin\":\"Ireland\"}").request().post("/api/whiskies").thenReturn().as(Whisky.class);
//        assertThat(whisky.getName()).isEqualToIgnoringCase("Jameson");
//        assertThat(whisky.getOrigin()).isEqualToIgnoringCase("Ireland");
//        assertThat(whisky.getId()).isNotZero();
//
//        // Check that it has created an individual resource, and check the content.
//        get("/api/whiskies/" + whisky.getId()).then()
//                .assertThat()
//                .statusCode(200)
//                .body("name", equalTo("Jameson"))
//                .body("origin", equalTo("Ireland"))
//                .body("id", equalTo(whisky.getId()));
//
//        // Delete the bottle
//        delete("/api/whiskies/" + whisky.getId()).then().assertThat().statusCode(204);
//
//        // Check that the resrouce is not available anymore
//        get("/api/whiskies/" + whisky.getId()).then()
//                .assertThat()
//                .statusCode(404);
//    }
}
