import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class User {

    //URL адрес endpoint для создания нового пользователя
    String createUserEndpointURL = "https://stellarburgers.nomoreparties.site/api/auth/register";
    //URL адрес endpoint для удаления пользователя
    String mainUserEndpointURL = "https://stellarburgers.nomoreparties.site/api/auth/user";

    //Служебный метод для генерации логина для тестового набора данных. В качестве параметра передается длинна генерируемой строки
    String userName = RandomStringUtils.randomAlphabetic(10);
    //Служебный метод для генерации пароля для тестового набора данных. В качестве параметра передается длинна генерируемой строки
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    //Служебный метод для генерации части email (до знака @) пользователя для тестового набора данных. В качестве параметра передается длинна генерируемой строки
    String userPartMailAdress = RandomStringUtils.randomAlphabetic(8).toLowerCase(Locale.ROOT);

    //Служебная переменная. Используется в тестах по изменению данных пользователя. Изменяет имя пользователя
    String newChangingUserName = "changed" + userName;
    //Служебная переменная. Используется в тестах по изменению данных пользователя. Изменяет email пользователя
    String newChangingEmail = "changed" + userPartMailAdress;

    //Служебная переменная. Используется в тестах по отправке уже занятого email на endpoint по изменению данных пользователя. Содержит имя пользователя
    String serviceUserNameForApiTest = "serviceName" + userName;
    //Служебная переменная. Используется в тестах по отправке уже занятого email на endpoint по изменению данных пользователя. Содержит email пользователя
    String serviceEmailForApiTest = "serviceemail" + userPartMailAdress;


    //Служебный метод для создания полного набора данных для регистрации нового пользователя
    @Step("Create full user random test registration data")
    public String createFullUserRandomTestRegData() {
        String regDataFull = "{\"email\":\"" + userPartMailAdress + "@testdata.ru" + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";
        return regDataFull;
    }

    //Служебный метод для создания набора данных для регистрации нового пользователя без генерации имени пользователя
    @Step("Create user random test registration data without user name")
    public String createUserRandomTestRegDataWithoutUsername() {
        String regDataWithoutUserName = "{\"email\":\"" + userPartMailAdress + "@testdata.ru" + "\","
                + "\"password\":\"" + userPassword + "\"}";
        return regDataWithoutUserName;
    }

    //Служебный метод для создания набора данных для регистрации нового пользователя без генерации пароля
    @Step("Create user random test registration data without password")
    public String createUserRandomTestRegDataWithoutPassword() {
        String regDataWithoutPassword = "{\"email\":\"" + userPartMailAdress + "@testdata.ru" + "\","
                + "\"name\":\"" + userName + "\"}";
        return regDataWithoutPassword;
    }

    //Служебный метод для создания набора данных для регистрации нового пользователя без генерации email адреса пользователя
    @Step("Create user random test registration data without email adress")
    public String createUserRandomTestRegDataWithoutEmailAdress() {
        String regDataWithoutEmailAdress = "{\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";
        return regDataWithoutEmailAdress;
    }

    //Служебный метод для отправки запроса на создание нового пользователя.
    //В качестве аргумента принимает один из тестовых методов по подготовке тестовых данных regData
    @Step("Create new user request sending")
    public Response createNewUserAPIRequest(String regDataVariant) {
        Response newCourierResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body(regDataVariant)
                .when()
                .post(createUserEndpointURL);
        return newCourierResponse;
    }

    //Служебный метод на удаление пользователя
    @Step("Delete user by token (/api/auth/user)")
    public Response deleteUserByAccessToken(String userToken) {
        Response userDeleteResponse = given()
                .header("Authorization", userToken)
               .and()
               .delete(mainUserEndpointURL);
        return userDeleteResponse;
    }

    //Служебный метод на получение данных пользователя
    @Step("Get user data by token (/api/auth/user)")
    public Response getUserDataByAccessToken(String userToken) {
        Response userDataResponse = given()
                .header("Authorization", userToken)
                .and()
                .get(mainUserEndpointURL);
        return userDataResponse;
    }

    //Служебный метод на изменение данных пользователя
    //В качестве аргумента принимает один из тестовых методов по подготовке тестовых данных по изменению данных пользователя
    @Step("Patch user data by token (/api/auth/user)")
    public Response patchUserDataByAccessToken(String userToken, String userDataVariant) {
        Response userDataResponse = given()
                .header("Authorization", userToken)
                .header("Content-type", "application/json")
                .and()
                .body(userDataVariant)
                .patch(mainUserEndpointURL);
        return userDataResponse;
    }

    //Служебный метод на изменение данных пользователя без передачи токена
    //В качестве аргумента принимает один из тестовых методов по подготовке тестовых данных по изменению данных пользователя но не передает авторизационный токен
    @Step("Patch user data without token (/api/auth/user)")
    public Response patchUserDataWithoutAccessToken(String userDataVariant) {
        Response userDataResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body(userDataVariant)
                .patch(mainUserEndpointURL);
        return userDataResponse;
    }

    //Служебный метод для создания полного набора данных для обновления информации пользователя
    @Step("Create full user data for user information patch")
    public String createFullUserPatchData() {
        String userPatchDataFull = "{\"email\":\"" + newChangingEmail + "@testdata.ru" + "\","
                + "\"name\":\"" + newChangingUserName + "\"}";
        return userPatchDataFull;
    }

    //Служебный метод для создания набора данных для обновления email пользователя
    @Step("Create full user data for user information patch")
    public String createEmailUserPatchData() {
        String userEmailPatchData = "{\"email\":\"" + newChangingEmail + "@testdata.ru" + "\"}";
        return userEmailPatchData;
    }

    //Служебный метод для создания набора данных для обновления имени пользователя
    @Step("Create full user data for user information patch")
    public String createNameUserPatchData() {
        String userNamePatchData = "{\"name\":\"" + newChangingUserName + "\"}";
        return userNamePatchData;
    }

    //Служебный метод для создания полного набора данных для регистрации нового пользователя
    //Используется в тесте по проверки отправки существующего email на endpoint по изменению данных пользователя
    @Step("Create full user test registration data for exist email test")
    public String createFullUserTestRegDataForExistEmailTest() {
        String regDataFull = "{\"email\":\"" + serviceEmailForApiTest + "@testdata.ru" + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + serviceUserNameForApiTest + "\"}";
        return regDataFull;
    }

    @Step("Create full user random test registration data")
    public String createFullUserRandomTestRegData111() {
        String regDataFull = "{\"email\":\"" + userPartMailAdress + "@testdata.ru" + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"name\":\"" + userName + "\"}";
        return regDataFull;
    }


}