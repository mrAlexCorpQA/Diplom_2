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
            Response deleteUserResponse = userTest.deleteUserByAccessToken(userAccessTokenFromResponse);
            deleteUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(202);
        }
    }

    //Проверка создания нового пользователя
    //Дополнительно присваивает переменной userAccessTokenFromResponse авторизационный токен для последующего удаления созданного пользователя
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
        Response deleteUserResponse = userTest.deleteUserByAccessToken(userAccessToken);
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

    //Проверка получения данных пользователя (технический тест, необходим для дальнейшего использования в других тестах по проверке API)
    @Test
    @DisplayName("Get user data (Service test)")
    @Description("Get user data (service test for /api/auth/user)")
    public void getUserDataAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .assertThat().body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
    }

    //Проверка изменения данных пользователя (пользователь авторизован, меняется email и имя пользователя)
    @Test
    @DisplayName("Change email and user name (user authorized)")
    @Description("Change user data (service test for /api/auth/user)")
    public void changeUserEmailAndNameAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getFirstUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getFirstUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserEmailAndNameResponse = userTest.patchUserDataByAccessToken(userAccessToken, userTest.createFullUserPatchData());
        newUserEmailAndNameResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.newChangingEmail + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.newChangingUserName));
    }

    //Проверка изменения данных пользователя (пользователь авторизован, меняется email)
    @Test
    @DisplayName("Change user email (user authorized)")
    @Description("Change user email (service test for /api/auth/user)")
    public void changeUserEmailAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getFirstUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getFirstUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserEmailResponse = userTest.patchUserDataByAccessToken(userAccessToken, userTest.createEmailUserPatchData());
        newUserEmailResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.newChangingEmail + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
    }

    //Проверка изменения данных пользователя (пользователь авторизован, меняется имя пользователя)
    @Test
    @DisplayName("Change user email (user authorized)")
    @Description("Change user email (service test for /api/auth/user)")
    public void changeUserNameAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getFirstUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getFirstUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserNameResponse = userTest.patchUserDataByAccessToken(userAccessToken, userTest.createNameUserPatchData());
        newUserNameResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.newChangingUserName));
    }

    //Проверка изменения данных пользователя (пользователь не авторизован, меняется email и имя пользователя)
    @Test
    @DisplayName("Change email and user name (user not authorized)")
    @Description("Change user data without authorization (service test for /api/auth/user)")
    public void changeUserEmailAndNameWithoutAuthorizationAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getFirstUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getFirstUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserEmailAndNameResponse = userTest.patchUserDataWithoutAccessToken(userTest.createFullUserPatchData());
        newUserEmailAndNameResponse.then().assertThat().body("success", equalTo(false)).and().statusCode(401)
                .body("message", equalTo("You should be authorised"));
        Response getSecondUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getSecondUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
    }

    //Проверка изменения данных пользователя (пользователь авторизован, меняется email)
    @Test
    @DisplayName("Change user email (user not authorized)")
    @Description("Change user email without authorization (service test for /api/auth/user)")
    public void changeUserEmailWithoutAuthorizationAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getFirstUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getFirstUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserEmailResponse = userTest.patchUserDataWithoutAccessToken(userTest.createEmailUserPatchData());
        newUserEmailResponse.then().assertThat().body("success", equalTo(false)).and().statusCode(401)
                .body("message", equalTo("You should be authorised"));
        Response getSecondUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getSecondUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));

    }

    //Проверка изменения данных пользователя (пользователь авторизован, меняется имя пользователя)
    @Test
    @DisplayName("Change user email (user not authorized)")
    @Description("Change user email without authorization (service test for /api/auth/user)")
    public void changeUserNameWithoutAuthorizationAPIServiceTest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = newUserResponse.then().extract().path("accessToken");
        Response getFirstUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getFirstUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserNameResponse = userTest.patchUserDataWithoutAccessToken(userTest.createNameUserPatchData());
        newUserNameResponse.then().assertThat().body("success", equalTo(false)).and().statusCode(401)
                .body("message", equalTo("You should be authorised"));
        Response getSecondUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getSecondUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
    }

    //Проверка изменения данных пользователя (пользователь авторизован, отправляется занятый другим пользователем email)
    @Test
    @DisplayName("Change email to already using by different user (user authorized)")
    @Description("Change user email to already using (service test for /api/auth/user)")
    public void changeAlreadyUsingEmailAPIServiceTest() {
        Response firstUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserTestRegDataForExistEmailTest());
        firstUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String firstUserAccessToken = firstUserResponse.then().extract().path("accessToken");
        Response secondUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        secondUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200);
        String userAccessToken = secondUserResponse.then().extract().path("accessToken");
        Response getSecondUserDataResponse = userTest.getUserDataByAccessToken(userAccessToken);
        getSecondUserDataResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .body("user.email", equalTo(userTest.userPartMailAdress + "@testdata.ru"))
                .and().assertThat().body("user.name", equalTo(userTest.userName));
        Response newUserExistEmailResponse = userTest.patchUserDataByAccessToken(userAccessToken, userTest.createFullUserTestRegDataForExistEmailTest());
        newUserExistEmailResponse.then().assertThat().body("success", equalTo(false)).and().statusCode(403)
                .body("message", equalTo("User with such email already exists"));
        Response deleteFirstUserByToken = userTest.deleteUserByAccessToken(firstUserAccessToken);
    }
}