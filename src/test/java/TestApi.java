import dataBaseController.WorkWithMongo;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

public class TestApi {
   private static Properties properties;
  private Basic basic;
  private WorkWithMongo withMongo;

  @BeforeTest
  public void init() throws IOException {

    FileInputStream fileInputStream = new FileInputStream(".\\src\\main\\java\\files\\env.properties");
    properties = new Properties();
    properties.load(fileInputStream);
    basic = new Basic(properties);
    withMongo = new WorkWithMongo(properties);
  }

  @Test(priority = 1)
  public void verifyStatusCodeEmptyDataAuth() {
    assertEquals(basic.getAuthResponse("", "").getStatusCode(), 401);
  }
  @Test(priority = 1)
  public void verifyStatusCodeCreateCreditionalsWithoutRegister() {
    assertEquals(basic.getNewCreditionals("fakeAuthToken","someId","someHash").statusCode(), 401);
  }
  @Test(priority = 2)
  public void verifyStatusCodeNonRegisteredDataAuth() {
    assertEquals(basic.getAuthResponse(properties.getProperty("incorrectMail"), properties.getProperty("validPassword")).getStatusCode(), 401);
  }

  @Test(priority = 3)
  public void verifyStatusCodeSendMailUnregisteredEmail() {
    assertEquals(basic.getSendToMailConfirmResponse(properties.getProperty("validMail")).getStatusCode(), 404);
  }

  @Test(priority = 4)
  public void verifyStatusCodeValidDataToRegister() {
    assertEquals(basic.getRegisterNewAccountResponse(properties.getProperty("validEmail"),properties.getProperty("nameUser") ,properties.getProperty("validPassword")).getStatusCode(), 200);
  }

  @Test(priority = 5)
  public void verifyStatusCodeSendMailRegisterAndNoConfirmedEmail() {
    assertEquals(basic.getSendToMailConfirmResponse(properties.getProperty("validEmail")).getStatusCode(), 200);
  }


  @Test(priority = 6)
  public void verifyStatusCodeNonConfirmEmailAuth() {
    assertEquals(basic.getAuthResponse(properties.getProperty("validEmail"), properties.getProperty("validPassword")).getStatusCode(), 403);
  }
  @Test(priority = 7)
  public void verifyStatusCodeWhereEmailExistsForRegistrationInSystem() {
    assertEquals(basic.getRegisterNewAccountResponse(properties.getProperty("validEmail"),properties.getProperty("nameUser") ,properties.getProperty("validPassword")).getStatusCode(), 422);
  }

  @Test(priority = 8)
  public void verifyStatusCodeSendMailConfirmedEmail() {
    withMongo.updateByDataDB(properties.getProperty("validEmail"), "emailVerified", "true");
    assertEquals(basic.getSendToMailConfirmResponse(properties.getProperty("validEmail")).getStatusCode(), 403);
  }

  @Test(priority = 9)
  public void verifyStatusCodeValidDataAuth() {
    assertEquals(basic.getAuthResponse(properties.getProperty("validEmail"), properties.getProperty("validPassword")).getStatusCode(), 200);
  }
  @Test(priority = 9)
  public void verifyStatusCodeCreateCreditionalsInvalidData() {
    System.out.println();
    assertEquals(basic.getNewCreditionals(basic.getJsonValueToParameter(basic.getAuthResponse(properties.getProperty("validEmail"),
            properties.getProperty("validPassword")), "id"),
            "3424242423","xzcxzc").getStatusCode(), 422);
  }
  @Test(priority = 10)
  public void verifyStatusCodeValidDataAuthWithUpperCaseEmail() {
    assertEquals(basic.getAuthResponse(properties.getProperty("validEmail").toUpperCase(), properties.getProperty("validPassword")).getStatusCode(), 200);
  }
  @Test(priority = 11)
  public void verifyStatusCodeInvalidPasswordAuth() {
    assertEquals(basic.getAuthResponse(properties.getProperty("validEmail"), "incorrect").getStatusCode(), 401);
  }
  @Test(priority = 12)
  public void verifyStatusCodeInvalidDataAuthWithUpperCasePassword() {
    assertEquals(basic.getAuthResponse(properties.getProperty("validEmail"), properties.getProperty("validPassword").toUpperCase()).getStatusCode(), 401);
  }

  @Test(priority = 13)
  public void verifyEmailRequestEqualsInResponseFormAuth() {
    assertEquals(basic.getJsonValueToParameter(basic.getAuthResponse(properties.getProperty("validEmail"), properties.getProperty("validPassword")
    ), "user.email"), properties.getProperty("validEmail"));
  }

  @Test(priority = 14)
  public void verifyUserIdEqualsInResponse() {
    assertEquals(basic.getJsonValueToParameter(basic.getAuthResponse(properties.getProperty("validEmail"), properties.getProperty("validPassword")), "user.id"),
            basic.getJsonValueToParameter(basic.getAuthResponse(properties.getProperty("validEmail"), properties.getProperty("validPassword")), "userId"));
  }

  @Test(priority = 15)
  public void verifyStatusCodeForLogout() {
    assertEquals(basic.doGetLogout(basic.getJsonValueToParameter(basic.getAuthResponse(properties.getProperty("validEmail"),
            properties.getProperty("validPassword")), "id")).getStatusCode(), 204);
  }

  @AfterTest
  public void cleanDB() {
   // withMongo.deleteDb();
  }


//  @Test
////  public void testStatus(){
////    withMongo.updateByDataDB("a.hleim@cheshire-cat.by","emailVerified" ,"true");
////   // System.out.println(user);
////  }
////  @Test
////  public void testGet(){
////    User user = withMongo.getByLogin("a.hleim@cheshire-cat.by");
////    System.out.println(user);
////  }


//  @Test(priority = 1)
//  public void verifyResponseBodyValidDataToRegister() {
//    assertEquals(basic.getJsonValueToParameter(basic.getRegisterNewAccountResponse(properties.getProperty("newEmailToBody"),
//            properties.getProperty("passwordToUpper")), "code"), "registered");
//  }


}
//если уже залогинен в системе, то подтверждение регистрации из письма не происходит
//подтвеждает только первый токен из письма регистрации. повторные не подтверждают
