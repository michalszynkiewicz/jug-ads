package com.example.ads;

import io.quarkus.test.junit.DisabledOnSubstrate;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class AdResourceTest {

    @Inject
    AdResource adResource;

    @Test
    public void shouldReturnEmptyMessageOnNoData() {
        given()
          .when().get("/ads?userId=John")
          .then()
             .statusCode(200)
             .body(is("Toruń JUG rocks, you know John?"));
    }

    @Test
    @DisabledOnSubstrate
    public void shouldPassProperAdForUserWithData() {
        adResource.consumeQuery("Michal|pizza");
        given()
              .when()
                .get("/ads?userId=Michal")
              .then()
                .statusCode(200)
                .body(is("Pizza? Check out kragleplacki.pl"));
    }

}