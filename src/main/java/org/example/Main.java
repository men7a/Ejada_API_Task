package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.*;

public class Main {

    static String baseUrl = "https://simple-books-api.glitch.me";
    static String accessToken;
    static String orderId;

    public static void main(String[] args) {
        setup();
        authenticate();
        getAllBooks();
        placeOrder();
        updateOrder();
        deleteOrder();
    }
    public static void setup() {
        RestAssured.baseURI = baseUrl;
    }

    public static void authenticate() {
        String randomEmail = "testuser" + System.currentTimeMillis() + "@example.com";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"clientName\": \"TestUser\", \"clientEmail\": \"" + randomEmail + "\" }")
                .when()
                .post("/api-clients/")
                .then()
                .statusCode(201)
                .extract()
                .response();

        accessToken = response.jsonPath().getString("accessToken");
        System.out.println("Access Token: " + accessToken);
    }


    public static void getAllBooks() {
        Response response = when()
                .get("/books")
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("All Books:\n" + response.asPrettyString());
    }

    public static void placeOrder() {
        Response response = given()
                .baseUri("https://simple-books-api.glitch.me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"bookId\": 1,\n" +
                        "  \"customerName\": \"Tester\"\n" +
                        "}")
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract()
                .response();

        orderId = response.jsonPath().getString("orderId");
        System.out.println("Order placed! ID: " + orderId);
    }


    public static void updateOrder() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body("{ \"customerName\": \"Updated User\" }")
                .when()
                .patch("/orders/" + orderId)
                .then()
                .statusCode(204);

        System.out.println("Order updated.");
    }

    public static void deleteOrder() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/orders/" + orderId)
                .then()
                .statusCode(204);

        System.out.println("Order deleted.");
    }
}
