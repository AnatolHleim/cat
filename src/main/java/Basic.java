import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

import java.util.Properties;

class Basic extends RestAssured {
  private Properties properties;

  Basic(Properties properties) {
    RestAssured.baseURI = properties.getProperty("HOST");
    this.properties = properties;
  }

  Response getRegisterNewAccountResponse(String mail, String nameUser, String password) {
    return given().
            contentType(ContentType.JSON).
            config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).
            body(String.format(properties.getProperty("registerBody"), mail, nameUser, password)).
            when().
            post("api/MyUsers").
            then().
            contentType(ContentType.JSON).extract().response();
  }

  Response getSendToMailConfirmResponse(String mail) {
    return given().
            contentType(ContentType.JSON).
            body("{\"email\":\"" + mail + "\"}").
            when().
            post("api/MyUsers/resend").
            then().
            contentType(ContentType.JSON).extract().response();
  }

  Response getAuthResponse(String mail, String password) {
    return given().
            queryParam("include", "user").
            contentType(ContentType.JSON).
            body(String.format(properties.getProperty("authBody"), mail, password)).
            when().
            post("api/MyUsers/login").
            then().
            contentType(ContentType.JSON).extract().response();
  }

  String getJsonValueToParameter(Response response, String jsonParameter) {
    ResponseBody body = response.getBody();
    JsonPath jsonPathEvaluator = body.jsonPath();
    return jsonPathEvaluator.get(jsonParameter);

  }

  Response doGetLogout(String authToken) {
    return given().
            config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).
            queryParam("access_token", authToken).
            when().
            post("api/MyUsers/logout").
            then().
            extract().response();
  }

  Response getNewCreditionals(String authToken, String id, String hash) {
    return given().
            header("Authorization", authToken).
            contentType(ContentType.JSON).
            body(String.format(properties.getProperty("newCreditionalsBody"), id, hash)).
            when().
            post("api/Creditionals/addNew").
            then().
            extract().response();

  }
}
