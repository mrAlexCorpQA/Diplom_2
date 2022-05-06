import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;

import static io.restassured.RestAssured.given;

public class User {

    //URL адрес endpoint для создания нового пользователя
    String createUserEndpointURL = "https://stellarburgers.nomoreparties.site/api/auth/register";
    //URL адрес endpoint для удаления пользователя
    String deleteUserEndpointURL = "https://stellarburgers.nomoreparties.site/api/auth/user";


    //Служебный метод для генерации логина для тестового набора данных. В качестве параметра передается длинна генерируемой строки
    String userName = RandomStringUtils.randomAlphabetic(10);
    //Служебный метод для генерации пароля для тестового набора данных. В качестве параметра передается длинна генерируемой строки
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    //Служебный метод для генерации части email (до знака @) пользователя для тестового набора данных. В качестве параметра передается длинна генерируемой строки
    String userPartMailAdress = RandomStringUtils.randomAlphabetic(8);


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
    public Response deleteUserByAcessToken(String userToken) {
        Response userDeleteResponse = given()
                .header("Authorization", userToken)
               .and()
               .delete(deleteUserEndpointURL);
        return userDeleteResponse;

    }
}