import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserTest {


    User userTest;
    String userAccessTokenFromResponse;

    @Before
    public void setUp() {
        userTest = new User();
    }

    //Служебный метод по удалению созданных пользователей после прохождения тестов
    @After
    public void deleteCreatedUserAfterTest() {
        if (userAccessTokenFromResponse != null) {
            Response deleteUserResponse = userTest.deleteUserByAcessToken(userAccessTokenFromResponse);
            deleteUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(202);
        }
    }

    //Проверка создания нового пользователя (дополнительно присваивает переменной userAccessTokenFromResponse авторизационный токен для последующего удаления созданного пользователя)
    @Test
    @DisplayName("Create new user")
    @Description("New user creation test for /api/auth/register")
    public void createNewUserAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);;
        userAccessTokenFromResponse = newUserResponse.then().extract().path("accessToken");

    }

    //Проверка удаления нового пользователя (технический тест, необходим для дальнейшего использования в других тестах по проверке API)
    @Test
    @DisplayName("Delete created user (Service test)")
    @Description("Delete created user (service test for /api/auth/user)")
    public void deleteCreatedUserAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response deleteUserResponse = userTest.deleteUserByAcessToken(userAccessToken);
        deleteUserResponse.then().assertThat().body("success", equalTo(true))
                .and().assertThat().body("message", equalTo("User successfully removed")).and().statusCode(202);
    }

    //Проверка создания двух одинаковых пользователей
    @Test
    @DisplayName("Create two same users")
    @Description("Creation of two same users test for /api/auth/register")
    public void createTwoSameUsersAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        Response secondSendTryUsersResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        secondSendTryUsersResponse.then().assertThat().body("message", equalTo("User already exists"))
                .and().assertThat().body("success", equalTo(false)).and().statusCode(403);
    }

    //Проверка создания нового пользователя без передачи имени пользователя
    @Test
    @DisplayName("Create new user without username")
    @Description("New user creation test without username for /api/auth/register")
    public void createNewUserWithoutUserNameAPITest() {
        Response newUserWithoutUserNameResponse = userTest.createNewUserAPIRequest(userTest.createUserRandomTestRegDataWithoutUsername());
        newUserWithoutUserNameResponse.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields")).and().statusCode(403);
    }

    //Проверка создания нового пользователя без передачи пароля
    @Test
    @DisplayName("Create new user without Password")
    @Description("New user creation test without Password for /api/auth/register")
    public void createNewUserWithoutPasswordAPITest() {
        Response newUserWithoutPasswordResponse = userTest.createNewUserAPIRequest(userTest.createUserRandomTestRegDataWithoutPassword());
        newUserWithoutPasswordResponse.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields")).and().statusCode(403);
    }

    //Проверка создания нового пользователя без передачи email
    @Test
    @DisplayName("Create new user without email")
    @Description("New user creation test without email for /api/auth/register")
    public void createNewUserWithoutEmailAPITest() {
        Response newUserWithoutEmailResponse = userTest.createNewUserAPIRequest(userTest.createUserRandomTestRegDataWithoutEmailAdress());
        newUserWithoutEmailResponse.then().assertThat().body("success", equalTo(false))
                .and().assertThat().body("message", equalTo("Email, password and name are required fields")).and().statusCode(403);
    }


}