package fr.ul.miage.chevrier.banque;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;
import fr.ul.miage.chevrier.banque.dto.AccountInput;
import fr.ul.miage.chevrier.banque.entity.Account;
import fr.ul.miage.chevrier.banque.repository.AccountRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import java.util.Date;
import java.util.UUID;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccountTest extends GlobalTest {
    @LocalServerPort
    int port;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void setupContext() {
        accountRepository.deleteAll();
        RestAssured.port = port;
    }

    @Test
    public void test1Ping() {
        when().get(URI_ACCOUNTS).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void test2GetAllOkOnlyActive() {
        accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        when().get(URI_ACCOUNTS)
              .then()
              .statusCode(HttpStatus.SC_OK)
              .and()
              .assertThat()
              .body("_embedded.accountViews.size()", equalTo(3));
    }

    @Test
    public void test3GetAllOkActiveAndInactive() {
        accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), false));
        accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), false));
        when().get(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .assertThat()
                .body("_embedded.accountViews.size()", equalTo(1));
    }

    @Test
    public void test4GetOneOkActive() {
        var now = new Date();
        var account = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
                "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));
        Response response = when().get(URI_ACCOUNTS + URL_PART_SEPARATOR + account.getId())
                                  .then()
                                  .statusCode(HttpStatus.SC_OK)
                                  .extract()
                                  .response();
        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(account.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Lea"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsen"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));
    }

    @Test
    public void test5GetOneNotFoundActive() {
        var account = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
                "533380006", "330762399782","FR7630001007941234567890185", "secret", 0.0, new Date(), false));
        when().get(URI_ACCOUNTS + URL_PART_SEPARATOR + account.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void test6GetOneNotFoundNothing() {
        when().get(URI_ACCOUNTS + URL_PART_SEPARATOR + UUID.randomUUID().toString())
              .then()
              .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void test7GetOneBadRequestBadFormatId() {
        when().get(URI_ACCOUNTS + URL_PART_SEPARATOR + "formatUUIDMauvais")
              .then()
              .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test8PostCreated()  {
        var accountsBefore = accountRepository.findAll();
        assertThat(accountsBefore.size(), equalTo(0));

        var now = new Date();
        var response = given().body(toJSONString(new AccountInput("Lea", "Olsen", now,
                                                "France", "533380006", "330762399782",
                                                "FR7630001007941234567890185")))
                                               .contentType(ContentType.JSON)
                                               .when()
                                               .post(URI_ACCOUNTS)
                                               .then()
                                               .statusCode(HttpStatus.SC_CREATED)
                                               .extract()
                                               .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Lea"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsen"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("France"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountsAfter = accountRepository.findAll();
        assertThat(accountsAfter.size(), equalTo(1));

        var account = accountsAfter.get(0);
        assertThat(account.getFirstName(), equalTo("Lea"));
        assertThat(account.getLastName(), equalTo("Olsen"));
        assertThat(account.getCountry(), equalTo("France"));
        assertThat(account.getPassportNumber(), equalTo("533380006"));
        assertThat(account.getPhoneNumber(), equalTo("330762399782"));
        assertThat(account.getIBAN(), equalTo("FR7630001007941234567890185"));
    }

    @Test
    public void test9PostBadRequestBadValidationFirstNameNull() {
        given().body(toJSONString(new AccountInput(null, "Olsen", new Date(), "France",
              "533380006", "330762399782", "FR7630001007941234567890185")))
              .contentType(ContentType.JSON)
              .when()
              .post(URI_ACCOUNTS)
              .then()
              .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test10PostBadRequestBadValidationFirstNameBlank() {
        given().body(toJSONString(new AccountInput("", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test11PostBadRequestBadValidationLastNameNull() {
        given().body(toJSONString(new AccountInput("Léa", null, new Date(), "France",
               "533380006", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test12PostBadRequestBadValidationLastNameBlank() {
        given().body(toJSONString(new AccountInput("Léa", "", new Date(), "France",
                "533380006", "330762399782","FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test13PostBadRequestBadValidationBirthDateNull() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", null, "France",
                "533380006", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test14PostBadRequestBadValidationCountryNull() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), null,
                "533380006", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test15PostBadRequestBadValidationCountryBlank() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "",
                "533380006", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test16PostBadRequestBadValidationPassportNumberNull() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "France",
               null, "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test17PostBadRequestBadValidationPassportNumberBlank() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "France",
               "", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test18PostBadRequestBadValidationPassportNumberBadLength() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "France",
                "53338000", "330762399782", "FR7630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test19PostBadRequestBadValidationIBANNull() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "France",
                "533380006", "330762399782", null)))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test20PostBadRequestBadValidationIBANBlank() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "France",
               "533380006", "330762399782", "")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test21PostBadRequestBadValidationIBANBadLength() {
        given().body(toJSONString(new AccountInput("Léa", "Olsen", new Date(), "France",
                "533380006", "330762399782", "FR763000100794123456789018577575646476867899")))
                .contentType(ContentType.JSON)
                .when()
                .post(URI_ACCOUNTS)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test22PutOk()  {
        var now = new Date();
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));

        assertThat(accountBefore.getFirstName(), equalTo("Lea"));
        assertThat(accountBefore.getLastName(), equalTo("Olsen"));
        assertThat(accountBefore.getCountry(), equalTo("France"));
        assertThat(accountBefore.getPassportNumber(), equalTo("533380006"));
        assertThat(accountBefore.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountBefore.getIBAN(), equalTo("FR7630001007941234567890185"));

        var now2 = new Date();
        var response = given().body(toJSONString(new AccountInput("Leo", "Olsan", now2,"England",
                                        "433380006", "330762399782", "EN9630001007941234567890185")))
                                        .contentType(ContentType.JSON)
                                        .when()
                                        .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                                        .then()
                                        .statusCode(HttpStatus.SC_OK)
                                        .extract()
                                        .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(accountBefore.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Leo"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsan"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("England"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("EN9630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountAfter = accountRepository.find(accountBefore.getId()).get();
        assertThat(accountAfter.getFirstName(), equalTo("Leo"));
        assertThat(accountAfter.getLastName(), equalTo("Olsan"));
        assertThat(accountAfter.getCountry(), equalTo("England"));
        assertThat(accountAfter.getPassportNumber(), equalTo("433380006"));
        assertThat(accountAfter.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountAfter.getIBAN(), equalTo("EN9630001007941234567890185"));
    }

    @Test
    public void test23PutBadRequestBadValidationFirstNameNull() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, "Olsan", new Date(), "England",
                "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test24PutBadRequestBadValidationFirstNameBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("", "Olsan", new Date(),"England",
                "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test25PutBadRequestBadValidationLastNameNull() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", null, new Date(),"England",
                "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test26PutBadRequestBadValidationLastNameBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "", new Date(),"England",
               "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test27PutBadRequestBadValidationBirthDateNull() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", null,
                "England", "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test28PutBadRequestBadValidationCountryNull() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(), null,
                "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test29PutBadRequestBadValidationCountryBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(), "",
                "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test30PutBadRequestBadValidationPassportNumberNull() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(), "France",
                null, "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test31PutBadRequestBadValidationPassportNumberBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(), "France",
               "", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test32PutBadRequestBadValidationPassportNumberBadLength() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "olsan", new Date(),"France",
               "4333800068768546", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test33PutBadRequestBadValidationIBANNull() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(),"France",
               "433380006", "330762399782", null)))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test34PutBadRequestBadValidationIBANBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(), "France",
               "433380006", "330762399782", "")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test35PutBadRequestBadValidationIBANBadLength() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(),"France",
               "433380006", "330762399782", "EN96300010079412345678901858957497845674567567988")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test36PutBadRequestBadFormatId() {
        given().body(toJSONString(new AccountInput("Leo", "Olsan", new Date(),"France",
               "433380006", "330762399782", "EN9630001007941234567890185")))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + "formatUUIDMauvais")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test37PatchOkFirstName() {
        var now = new Date();
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));

        assertThat(accountBefore.getFirstName(), equalTo("Lea"));
        assertThat(accountBefore.getLastName(), equalTo("Olsen"));
        assertThat(accountBefore.getCountry(), equalTo("France"));
        assertThat(accountBefore.getPassportNumber(), equalTo("533380006"));
        assertThat(accountBefore.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountBefore.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountBefore.getSecret(), equalTo("secret"));
        assertThat(accountBefore.getBalance(), equalTo(0.0));
        assertThat(accountBefore.isActive(), equalTo(true));

        var response = given().body(toJSONString(new AccountInput("Leo", null, null,
                                          null, null, null, null)))
                                          .contentType(ContentType.JSON)
                                          .when()
                                          .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                                          .then()
                                          .statusCode(HttpStatus.SC_OK)
                                          .extract()
                                          .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(accountBefore.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Leo"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsen"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("France"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountAfter = accountRepository.find(accountBefore.getId()).get();
        assertThat(accountAfter.getFirstName(), equalTo("Leo"));
        assertThat(accountAfter.getLastName(), equalTo("Olsen"));
        assertThat(accountAfter.getCountry(), equalTo("France"));
        assertThat(accountAfter.getPassportNumber(), equalTo("533380006"));
        assertThat(accountAfter.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountAfter.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountAfter.getSecret(), equalTo("secret"));
        assertThat(accountAfter.getBalance(), equalTo(0.0));
        assertThat(accountAfter.isActive(), equalTo(true));
    }

    @Test
    public void test38PatchOkLastName() {
        var now = new Date();
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));

        assertThat(accountBefore.getFirstName(), equalTo("Lea"));
        assertThat(accountBefore.getLastName(), equalTo("Olsen"));
        assertThat(accountBefore.getCountry(), equalTo("France"));
        assertThat(accountBefore.getPassportNumber(), equalTo("533380006"));
        assertThat(accountBefore.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountBefore.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountBefore.getSecret(), equalTo("secret"));
        assertThat(accountBefore.getBalance(), equalTo(0.0));
        assertThat(accountBefore.isActive(), equalTo(true));

        var response = given().body(toJSONString(new AccountInput(null, "Olsan", null,
                                        null, null, null, null)))
                                        .contentType(ContentType.JSON)
                                        .when()
                                        .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                                        .then()
                                        .statusCode(HttpStatus.SC_OK)
                                        .extract()
                                        .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(accountBefore.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Lea"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsan"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("France"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountAfter = accountRepository.find(accountBefore.getId()).get();
        assertThat(accountAfter.getFirstName(), equalTo("Lea"));
        assertThat(accountAfter.getLastName(), equalTo("Olsan"));
        assertThat(accountAfter.getCountry(), equalTo("France"));
        assertThat(accountAfter.getPassportNumber(), equalTo("533380006"));
        assertThat(accountAfter.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountAfter.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountAfter.getSecret(), equalTo("secret"));
        assertThat(accountAfter.getBalance(), equalTo(0.0));
        assertThat(accountAfter.isActive(), equalTo(true));
    }

    @Test
    public void test39PatchOkCountry() {
        var now = new Date();
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));

        assertThat(accountBefore.getFirstName(), equalTo("Lea"));
        assertThat(accountBefore.getLastName(), equalTo("Olsen"));
        assertThat(accountBefore.getCountry(), equalTo("France"));
        assertThat(accountBefore.getPassportNumber(), equalTo("533380006"));
        assertThat(accountBefore.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountBefore.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountBefore.getSecret(), equalTo("secret"));
        assertThat(accountBefore.getBalance(), equalTo(0.0));
        assertThat(accountBefore.isActive(), equalTo(true));

        var response = given().body(toJSONString(new AccountInput(null, null, null,
                                       "England", null, null, null)))
                                        .contentType(ContentType.JSON)
                                        .when()
                                        .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                                        .then()
                                        .statusCode(HttpStatus.SC_OK)
                                        .extract()
                                        .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(accountBefore.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Lea"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsen"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("England"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountAfter = accountRepository.find(accountBefore.getId()).get();
        assertThat(accountAfter.getFirstName(), equalTo("Lea"));
        assertThat(accountAfter.getLastName(), equalTo("Olsen"));
        assertThat(accountAfter.getCountry(), equalTo("England"));
        assertThat(accountAfter.getPassportNumber(), equalTo("533380006"));
        assertThat(accountAfter.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountAfter.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountAfter.getSecret(), equalTo("secret"));
        assertThat(accountAfter.getBalance(), equalTo(0.0));
        assertThat(accountAfter.isActive(), equalTo(true));
    }

    @Test
    public void test39PatchOkPassportNumber() {
        var now = new Date();
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));

        assertThat(accountBefore.getFirstName(), equalTo("Lea"));
        assertThat(accountBefore.getLastName(), equalTo("Olsen"));
        assertThat(accountBefore.getCountry(), equalTo("France"));
        assertThat(accountBefore.getPassportNumber(), equalTo("533380006"));
        assertThat(accountBefore.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountBefore.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountBefore.getSecret(), equalTo("secret"));
        assertThat(accountBefore.getBalance(), equalTo(0.0));
        assertThat(accountBefore.isActive(), equalTo(true));

        var response = given().body(toJSONString(new AccountInput(null, null, null,
                                        null,"993380006", null, null)))
                                        .contentType(ContentType.JSON)
                                        .when()
                                        .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                                        .then()
                                        .statusCode(HttpStatus.SC_OK)
                                        .extract()
                                        .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(accountBefore.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Lea"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsen"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("France"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7630001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountAfter = accountRepository.find(accountBefore.getId()).get();
        assertThat(accountAfter.getFirstName(), equalTo("Lea"));
        assertThat(accountAfter.getLastName(), equalTo("Olsen"));
        assertThat(accountAfter.getCountry(), equalTo("France"));
        assertThat(accountAfter.getPassportNumber(), equalTo("993380006"));
        assertThat(accountAfter.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountAfter.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountAfter.getSecret(), equalTo("secret"));
        assertThat(accountAfter.getBalance(), equalTo(0.0));
        assertThat(accountAfter.isActive(), equalTo(true));
    }

    @Test
    public void test40PatchOkIBAN() {
        var now = new Date();
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", now, "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, now, true));

        assertThat(accountBefore.getFirstName(), equalTo("Lea"));
        assertThat(accountBefore.getLastName(), equalTo("Olsen"));
        assertThat(accountBefore.getCountry(), equalTo("France"));
        assertThat(accountBefore.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountBefore.getPassportNumber(), equalTo("533380006"));
        assertThat(accountBefore.getIBAN(), equalTo("FR7630001007941234567890185"));
        assertThat(accountBefore.getSecret(), equalTo("secret"));
        assertThat(accountBefore.getBalance(), equalTo(0.0));
        assertThat(accountBefore.isActive(), equalTo(true));

        var response = given().body(toJSONString(new AccountInput(null, null, null,
                                        null, null, null, "FR7990001007941234567890185")))
                                        .contentType(ContentType.JSON)
                                        .when()
                                        .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                                        .then()
                                        .statusCode(HttpStatus.SC_OK)
                                        .extract()
                                        .response();

        String JSONAsString = response.asString();
        assertThat(JSONAsString,containsString("id"));
        assertThat(JSONAsString,containsString(accountBefore.getId().toString()));
        assertThat(JSONAsString,containsString("firstName"));
        assertThat(JSONAsString,containsString("Lea"));
        assertThat(JSONAsString,containsString("lastName"));
        assertThat(JSONAsString,containsString("Olsen"));
        assertThat(JSONAsString,containsString("country"));
        assertThat(JSONAsString,containsString("France"));
        assertThat(JSONAsString,containsString("iban"));
        assertThat(JSONAsString,containsString("FR7990001007941234567890185"));
        assertThat(JSONAsString,containsString("balance"));
        assertThat(JSONAsString,containsString("0.0"));
        assertThat(JSONAsString,containsString("dateAdded"));
        assertThat(JSONAsString,not(containsString("birthDate")));
        assertThat(JSONAsString,not(containsString("passportNumber")));
        assertThat(JSONAsString,not(containsString("phoneNumber")));
        assertThat(JSONAsString,not(containsString("secret")));
        assertThat(JSONAsString,not(containsString("active")));

        var accountAfter = accountRepository.find(accountBefore.getId()).get();
        assertThat(accountAfter.getFirstName(), equalTo("Lea"));
        assertThat(accountAfter.getLastName(), equalTo("Olsen"));
        assertThat(accountAfter.getCountry(), equalTo("France"));
        assertThat(accountAfter.getPassportNumber(), equalTo("533380006"));
        assertThat(accountAfter.getPhoneNumber(), equalTo("330762399782"));
        assertThat(accountAfter.getIBAN(), equalTo("FR7990001007941234567890185"));
        assertThat(accountAfter.getSecret(), equalTo("secret"));
        assertThat(accountAfter.getBalance(), equalTo(0.0));
        assertThat(accountAfter.isActive(), equalTo(true));
    }

    @Test
    public void test41PatchBadRequestBadValidationFirstNameBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput("", null, null, null, null, null, null)))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test42PatchBadRequestBadValidationLastNameBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, "", null, null, null, null, null)))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test43PatchBadRequestBadValidationCountryBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, null, null, "", null, null, null)))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test44PatchBadRequestBadValidationPassportNumberBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
        "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, null, null, null, "", null, null)))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test45PatchBadRequestBadValidationPassportNumberBadLength() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, null, null, null, "53338000689999", null, null)))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test46PatchBadRequestBadValidationIBANBlank() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, null, null, null, null, null, "")))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test47PatchBadRequestBadValidationIBANBadLength() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
        "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));
        given().body(toJSONString(new AccountInput(null, null, null, null, null, null, "FR76300010079412345678901859999986756565")))
                .contentType(ContentType.JSON)
                .when()
                .patch(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);//TODO
    }

    @Test
    public void test48PatchBadRequestBadFormatId() {
        given().body(toJSONString(new AccountInput(null, null, null, null, null, null, null)))
                .contentType(ContentType.JSON)
                .when()
                .put(URI_ACCOUNTS + URL_PART_SEPARATOR + "formatUUIDMauvais")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void test49DeleteNoContentActive() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), true));

        var accountsBefore = accountRepository.findAll();
        assertThat(accountsBefore.size(), equalTo(1));

        given().delete(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        var accountsAfter = accountRepository.findAll();
        assertThat(accountsAfter.size(), equalTo(0));
    }

    @Test
    public void test50DeleteNoContentInactive() {
        var accountBefore = accountRepository.save(new Account(UUID.randomUUID(), "Lea", "Olsen", new Date(), "France",
       "533380006", "330762399782", "FR7630001007941234567890185", "secret", 0.0, new Date(), false));

        var accountsBefore = accountRepository.findAll();
        assertThat(accountsBefore.size(), equalTo(0));

        given().delete(URI_ACCOUNTS + URL_PART_SEPARATOR + accountBefore.getId().toString())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        var accountsAfter = accountRepository.findAll();
        assertThat(accountsAfter.size(), equalTo(0));
    }

    @Test
    public void test51DeleteNoContentNothing() {
        given().delete(URI_ACCOUNTS + URL_PART_SEPARATOR + UUID.randomUUID().toString())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void test52DeleteBadRequestBadFormatId() {
        given().delete(URI_ACCOUNTS + URL_PART_SEPARATOR + "formatUUIDMauvais")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}