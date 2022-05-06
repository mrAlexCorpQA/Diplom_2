import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Authorization {

    //URL адрес endpoint для авторизации пользователя
    String authorizationUserEndpointURL = "https://stellarburgers.nomoreparties.site/api/auth/login";

    //Служебный метод для создания полного набора данных для авторизации пользователя
    @Step("Create full user test authorization data")
    public String createFullUserAuthorizationData(String userPartMailAdress, String userPassword) {
        String authorizationDataFull = "{\"email\":\"" + userPartMailAdress + "@testdata.ru" + "\","
                + "\"password\":\"" + userPassword + "\"}";
        return authorizationDataFull;
    }

    //Служебный метод для создания набора данных для авторизации пользователя без передачи email пользователя
    @Step("Create user test authorization data without email")
    public String createUserAuthorizationDataWithoutEmail(String userPassword) {
        String authorizationDataWithoutEmail = "{\"email\":\"" + "\","
                + "\"password\":\"" + userPassword + "\"}";
        return authorizationDataWithoutEmail;
    }

    //Служебный метод для создания набора данных для авторизации пользователя без передачи пароля пользователя
    @Step("Create user test authorization data without password")
    public String createUserAuthorizationDataWithoutPassword(String userPartMailAdress) {
        String authorizationDataWithoutPassword = "{\"email\":\"" + userPartMailAdress + "@testdata.ru" + "\","
                + "\"password\":\"" + "\"}";
        return authorizationDataWithoutPassword;
    }

    //Служебный метод для отправки запроса на авторизацию пользователя.
    //В качестве аргумента принимает один из тестовых методов по подготовке тестовых данных AuthorizationData
    @Step("Create user authorization sending")
    public Response createUserAuthorizationAPIRequest(String regDataVariant) {
        Response authorizationUserUserResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body(regDataVariant)
                .when()
                .post(authorizationUserEndpointURL);
        return authorizationUserUserResponse;
    }


}