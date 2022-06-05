import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {

    User userTest;
    String userAccessTokenFromResponse;
    Order orderTest;
    Authorization authorizationTest;

    @Before
    public void setUp() {
        userTest = new User();
        authorizationTest = new Authorization();
        orderTest = new Order();
    }

    //Служебный метод по удалению созданных пользователей после прохождения тестов
    @After
    public void deleteCreatedUserAfterTest() {
        if (userAccessTokenFromResponse != null) {
            Response deleteUserResponse = userTest.deleteUserByAccessToken(userAccessTokenFromResponse);
            deleteUserResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(202);
        }
    }

    //Проверка создания нового заказа авторизованным пользователем
    //Дополнительно присваивает переменной userAccessTokenFromResponse авторизационный токен для последующего удаления созданного пользователя
    @Test
    @DisplayName("Create new order. User authorized")
    @Description("New order creation test for /api/orders")
    public void createNewOrderAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        userAccessTokenFromResponse = newUserResponse.then().extract().path("accessToken");
        Response newOrderResponse = orderTest.createNewOrderAPIRequestWithAuthToken(userAccessTokenFromResponse, orderTest.createNewOrderFullTestDataWithTwoIngredients());
        newOrderResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .and().assertThat().body("order.number", notNullValue());
    }

    //Проверка создания нового заказа не авторизованным пользователем
    @Test
    @DisplayName("Create new order without authorization")
    @Description("New order creation without authorization test for /api/orders")
    public void createNewOrderWithoutAuthorizationAPITest() {
        Response newOrderResponse = orderTest.createNewOrderAPIRequestWithoutAuthToken(orderTest.createNewOrderFullTestDataWithTwoIngredients());
        newOrderResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .and().assertThat().body("order.number", notNullValue());
    }

    //Проверка создания нового заказа без ингридиентов
    @Test
    @DisplayName("Create new order without sending ingredients")
    @Description("New order creation without sending ingredients test for /api/orders")
    public void createNewOrderWithoutSendingIngredientsAPITest() {
        Response newOrderResponse = orderTest.createNewOrderAPIRequestWithoutAuthToken(orderTest.createNewOrderFullTestDataWithoutIngredients());
        newOrderResponse.then().assertThat().body("success", equalTo(false)).and().statusCode(400)
                .and().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    //Проверка создания нового заказа с некорректным хешем ингридиентов
    @Test
    @DisplayName("Create new order with incorrect ingredients hash")
    @Description("New order creation with sending incorrect ingredients hash test for /api/orders")
    public void createNewOrderWithSendingIncorrectIngredientsHashAPITest() {
        Response newOrderResponse = orderTest.createNewOrderAPIRequestWithoutAuthToken(orderTest.createNewOrderFullTestDataWithIncorrectIngredientsHash());
        newOrderResponse.then().assertThat().statusCode(500);
    }


    //Проверка получения нового заказа авторизованным пользователем
    //Дополнительно присваивает переменной userAccessTokenFromResponse авторизационный токен для последующего использования и удаления созданного пользователя
    @Test
    @DisplayName("Get order list with user authorization")
    @Description("Get order list with user authorization for /api/orders")
    public void getOrderListWithAuthorizationAPITest() {
        Response newUserResponse = userTest.createNewUserAPIRequest(userTest.createFullUserRandomTestRegData());
        newUserResponse.then().assertThat().body("success", equalTo(true));
        userAccessTokenFromResponse = newUserResponse.then().extract().path("accessToken");
        Response newOrderResponse = orderTest.createNewOrderAPIRequestWithAuthToken(userAccessTokenFromResponse, orderTest.createNewOrderFullTestDataWithTwoIngredients());
        newOrderResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .and().assertThat().body("order.number", notNullValue());
        Response orderListResponse = orderTest.createOrderGetRequestWithAuthTokenAPIRequest(userAccessTokenFromResponse);
        orderListResponse.then().assertThat().body("success", equalTo(true)).and().statusCode(200)
                .and().assertThat().body("orders", notNullValue());
    }


    //Проверка получения списка заказов без авторизации
    @Test
    @DisplayName("Get order list without user authorization")
    @Description("Get order list without user authorization for /api/orders")
    public void getOrderListWithoutAuthorizationAPITest() {
        Response orderListResponse = orderTest.createOrderGetRequesWithoutAuthTokenAPIRequest();
        orderListResponse.then().assertThat().body("success", equalTo(false)).and().statusCode(401)
                .and().assertThat().body("message", equalTo("You should be authorised"));
    }

}