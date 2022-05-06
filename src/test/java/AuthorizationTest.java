import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class AuthorizationTest {


    User userTest;
    String userAccessTokenFromResponse;

    Authorization authorizationTest;

    @Before
    public void setUp() {
        userTest = new User();
        authorizationTest = new Authorization();
    }

    //Служебный метод по удалению созданных пользователей после прохождения тестов
    @After
    public void deleteCreatedUserAfterTest() {
        if (userAccessTokenFromResponse != null) {
            Response deleteUserResponse = userTest.deleteUserByAcessToken(userAccessTokenFromResponse);
            deleteUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(202);
        }
    }

    //Проверка авторизации пользователя
    @Test
    @DisplayName("User authorization")
    @Description("New user authorization test for /api/auth/login endpoint")
    public void userAutorizationAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        userAccessTokenFromResponse = newUserResponse.then().extract().path("accessToken");
        Response CourierIdResponse = authorizationTest.createUserAuthorizationAPIRequest(authorizationTest.createFullUserAuthorizationData(userTest.userPartMailAdress, userTest.userPassword));
        CourierIdResponse.then().assertThat().body("success", equalTo(true))
                .and().statusCode(200);
    }

    //Проверка авторизации пользователя без передачи email
    @Test
    @DisplayName("User authorization without email")
    @Description("New user authorization test without email for /api/auth/login endpoint")
    public void userAutorizationWithoutEmailAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        Response UserResponse = authorizationTest.createUserAuthorizationAPIRequest(authorizationTest.createUserAuthorizationDataWithoutEmail(userTest.userPassword));
        UserResponse.then().assertThat().body("success", equalTo(false))
                .and().statusCode(401);
    }

    //Проверка авторизации пользователя без передачи пароля
    @Test
    @DisplayName("User authorization without password")
    @Description("New user authorization test  without password for /api/auth/login endpoint")
    public void userAutorizationWithoutPasswordAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        Response UserResponse = authorizationTest.createUserAuthorizationAPIRequest(authorizationTest.createUserAuthorizationDataWithoutPassword(userTest.userPartMailAdress));
        UserResponse.then().assertThat().body("success", equalTo(false))
                .and().statusCode(401);
    }

    //Проверка авторизации пользователя с передачей некорректного email но корректного пароля
    @Test
    @DisplayName("User authorization authorization with incorrect email")
    @Description("New user authorization test with incorrect email for /api/auth/login endpoint")
    public void userAutorizationWithIncorrectEmailAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        Response UserResponse = authorizationTest.createUserAuthorizationAPIRequest(authorizationTest.createFullUserAuthorizationData("UncorrectEMail", userTest.userPassword));
        UserResponse.then().assertThat().body("success", equalTo(false))
                .and().statusCode(401);
    }

    //Проверка авторизации пользователя с передачей некорректного пароля но корректного email
    @Test
    @DisplayName("User authorization authorization with incorrect password")
    @Description("New user authorization test with incorrect password for /api/auth/login endpoint")
    public void userAutorizationWithIncorrectPasswordAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        Response UserResponse = authorizationTest.createUserAuthorizationAPIRequest(authorizationTest.createFullUserAuthorizationData(userTest.userPartMailAdress, "UncorrectPassword"));
        UserResponse.then().assertThat().body("success", equalTo(false))
                .and().statusCode(401);
    }


}