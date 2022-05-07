import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Order {

    //URL адрес endpoint для обработки заказов
    String orderProcessingEndpointURL = "https://stellarburgers.nomoreparties.site/api/orders";

    //Служебные параметры для тестов. Корректный хеш ингридиента 1
    String hashOfFirstIngredient = "61c0c5a71d1f82001bdaaa6d";
    //Служебные параметры для тестов. Корректный хеш ингридиента 2
    String hashOfSecondIngredient = "61c0c5a71d1f82001bdaaa6f";

    //Служебные параметры для тестов. Некорректный хеш ингридиента 1
    String incorrectHashOfFirstIngredient = "9testHash";
    //Служебные параметры для тестов. Некорректный хеш ингридиента 2
    String incorrectHashOfSecondIngredient = "99testHash";

    //Служебный метод для создания набора данных для оформления нового заказа (передается 2 ингредиента)
    @Step("Create new order data with two ingredients")
    public String createNewOrderFullTestDataWithTwoIngredients() {
        String orderDataFull = "{\"ingredients\": [\"" + hashOfFirstIngredient + "\",\"" + hashOfSecondIngredient + "\"]}";
        return orderDataFull;
    }

    //Служебный метод для создания набора данных для оформления нового заказа (без ингредиентов)
    @Step("Create new order data without ingredients")
    public String createNewOrderFullTestDataWithoutIngredients() {
        String orderDataFull = "{\"ingredients\": [" + "]}";
        return orderDataFull;
    }

    //Служебный метод для создания набора данных для оформления нового заказа (передается некорректный хеш ингредиентов)
    @Step("Create new order data with incorrect ingredients hash")
    public String createNewOrderFullTestDataWithIncorrectIngredientsHash() {
        String orderDataFull = "{\"ingredients\": [\"" + incorrectHashOfFirstIngredient + "\",\"" + incorrectHashOfSecondIngredient + "\"]}";
        return orderDataFull;
    }

    //Служебный метод для отправки запроса на создание нового заказа с передачей авторизационного токена пользователя.
    //В качестве аргумента принимает один из тестовых методов по подготовке тестовых данных и авторизационный токен
    @Step("Create new order request sending with auth token")
    public Response createNewOrderAPIRequestWithAuthToken(String userToken, String orderDataVariant) {
        Response newOrderResponse = given()
                .header("Authorization", userToken)
                .header("Content-type", "application/json")
                .and()
                .body(orderDataVariant)
                .when()
                .post(orderProcessingEndpointURL);
        return newOrderResponse;
    }

    //Служебный метод для отправки запроса на создание нового заказа без передачи авторизационного токена пользователя.
    //В качестве аргумента принимает один из тестовых методов по подготовке тестовых данных
    @Step("Create new order request sending without auth token")
    public Response createNewOrderAPIRequestWithoutAuthToken(String orderDataVariant) {
        Response newOrderResponse = given()
                .header("Content-type", "application/json")
                .and()
                .body(orderDataVariant)
                .when()
                .post(orderProcessingEndpointURL);
        return newOrderResponse;
    }

    //Служебный метод для получение списка заказов без передачи авторизационного токена пользователя
    @Step("Create order list request sending without auth token")
    public Response createOrderGetRequesWithoutAuthTokenAPIRequest() {
        Response orderListResponse = given()
                .header("Content-type", "application/json")
                .when()
                .get(orderProcessingEndpointURL);
        return orderListResponse;
    }

    //Служебный метод для получение списка заказов с передачей авторизационного токена пользователя
    @Step("Create order list request sending with auth token")
    public Response createOrderGetRequestWithAuthTokenAPIRequest(String userToken) {
        Response orderListResponse = given()
                .header("Authorization", userToken)
                .header("Content-type", "application/json")
                .when()
                .get(orderProcessingEndpointURL);
        return orderListResponse;
    }


}