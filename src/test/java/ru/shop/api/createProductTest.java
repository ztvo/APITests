package ru.shop.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import ru.shop.api.helpers.HttpRequestProduct;
import ru.shop.api.helpers.HttpRequestProduct.PostRequestType;
import ru.shop.api.helpers.Product;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

public class createProductTest {
    String addedId;

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта")
    @Description("""
                    1. Добавление продукта
                    2. Получение данных о продукте
                    3. Проверка данных о продукте
            """)

    public void createProduct(String baseUrl, String id, String categoryId, String title, String alias, String content, String price, String oldPrice,
                              String status, String keywords, String description, String hit) throws IOException {
        System.out.println("Создание продукта - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);
        // проверка полей
        checkProduct(productInfo, returnedProduct);
    }

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "invalidPrice", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c невалидной ценой")
    @Description("""
                    Добавление продукта c невалидной ценой
            """)

    public void createProductWithInvalidPrice(String baseUrl, String id, String categoryId, String title,
                                              String alias, String content, String price, String oldPrice,
                                              String status, String keywords, String description, String hit)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c невалидной ценой - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("1265 Data truncated for column 'price' at row 1"),
                "Ожидалось что добавление продукта c невалидной ценой упадет с ошибкой");
    }

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "invalidOldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c невалидной oldPrice")
    @Description("""
                   Добавление продукта c невалидной oldPrice
            """)

    public void createProductWithInvalidOldPrice(String baseUrl, String id, String categoryId, String title,
                                              String alias, String content, String price, String oldPrice,
                                              String status, String keywords, String description, String hit)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c невалидной oldPrice - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("1265 Data truncated for column 'old_price' at row 1"),
                "Ожидалось что добавление продукта c невалидной старой ценой упадет с ошибкой");
    }

    @Parameters({"baseUrl", "id", "invalidCategoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c невалидной CategoryId")
    @Description("""
                   Добавление продукта c невалидной CategoryId
            """)
    public void createProductWithInvalidCategoryId(String baseUrl, String id, String categoryId, String title,
                                                 String alias, String content, String price, String oldPrice,
                                                 String status, String keywords, String description, String hit)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c невалидной CategoryId - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        Assert.assertNull(returnedProduct, "Ожидалось добавление продукта с невалидной категорий не произошло");
    }

    @Parameters({"baseUrl", "id", "boundaryCategoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c граничными значениями для categoryId")
    @Description("""
                   Добавление продукта c граничными значениями для CategoryId
            """)
    public void createProductBoundayCategoryId(String baseUrl, String id, String categoryId, String title,
                                                   String alias, String content, String price, String oldPrice,
                                                   String status, String keywords, String description, String hit)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c граничными значениями для CategoryId - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        Assert.assertNotNull(returnedProduct, "Ожидалось добавление продукта с граничным значением для категории id произошло");
        // проверка полей
        checkProduct(productInfo, returnedProduct);
    }

    @Parameters({"baseUrl", "id", "boundaryCategoryId", "invalidTitle", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c невалидным title")
    @Description("""
                   Добавление продукта c невалидным title
            """)
    public void createProductInvalidTitle(String baseUrl, String id, String categoryId, String title,
                                               String alias, String content, String price, String oldPrice,
                                               String status, String keywords, String description, String hit)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c невалидным title - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("String data, right truncated: 1406 Data too long for column 'title' at row"),
                "Ожидалось что добавление продукта c невалидной title упадет с ошибкой");
    }

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit", "invalidStatus"})
    @Test(testName = "Создание продукта c невалидным status")
    @Description("""
                   Добавление продукта c невалидным status:
                   Проверка: продукт добавился с status = 1   
            """)
    public void createProductWithInvalidStatus(String baseUrl, String id, String categoryId, String title,
                                                   String alias, String content, String price, String oldPrice,
                                                   String status, String keywords, String description, String hit, String invalidStatus)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c невалидным Status - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, invalidStatus, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        // проверка полей
        productInfo.setStatus(status);
        checkProduct(productInfo, returnedProduct);
    }

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit", "invalidHit"})
    @Test(testName = "Создание продукта c невалидным hit")
    @Description("""
                   Добавление продукта c невалидным hit 
                   Проверка: продукт добавился со значением hit = 1
            """)
    public void createProductWithInvalidHit(String baseUrl, String id, String categoryId, String title,
                                               String alias, String content, String price, String oldPrice,
                                               String status, String keywords, String description, String hit, String invalidHit)
            throws IOException, JsonParseException {
        System.out.println("Создание продукта c невалидным hit - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                invalidHit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        // проверка полей
        productInfo.setHit(hit);
        checkProduct(productInfo, returnedProduct);
    }

    @Parameters({"baseUrl", "id", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c пустой categoryId")
    @Description("""
                    Добавление продукта c пустой category id
            """)

    public void createProductWithEmptyCategoryId(String baseUrl, String id, String title, String alias, String content, String price, String oldPrice,
                                             String status, String keywords, String description, String hit) throws IOException, JsonParseException {
        System.out.println("Создание продукта c пустой categoryId - sent request for create product");
        Product productInfo = new Product(id, "", title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("1366 Incorrect integer value: '' for column 'category_id' at row 1"),
                "Ожидалось что добавление продукта c пустой категорией упадет с ошибкой");
    }

    @Parameters({"baseUrl", "categoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c пустым Id")
    @Description("""
                    Добавление продукта c пустым Id
            """)

    public void createProductWithEmptyId(String baseUrl, String categoryId, String title, String alias, String content, String price, String oldPrice,
                                                 String status, String keywords, String description, String hit) throws IOException, JsonParseException {
        System.out.println("Создание продукта c пустым Id - sent request for create product");
        Product productInfo = new Product(null, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        Assert.assertNotNull(returnedProduct, "Ожидалось добавление продукта с пустым id произошло");
        // проверка полей
        checkProduct(productInfo, returnedProduct);
    }

    @Parameters({"baseUrl", "categoryId", "price", "oldPrice", "status", "hit"})
    @Test(testName = "Создание продукта c пустым текстовыми полями")
    @Description("""
                    Добавление продукта c пустым текстовыми полями: title, alias, content, keywords, description 
            """)

    public void createProductWithEmptyTextFields(String baseUrl, String categoryId, String price, String oldPrice,
                                         String status, String hit) throws IOException, JsonParseException {
        System.out.println("Создание продукта c пустым текстовыми полями - sent request for create product");
        Product productInfo = new Product(null, categoryId, "", null, null, price, oldPrice, status, null, null,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на добавление не соотвествует ожидаемуму значение 200");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемуму значение 200");

        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        Assert.assertNotNull(returnedProduct, "Ожидалось добавление продукта с пустым текстовыми полями произошло");
        // проверка полей
        checkProduct(productInfo, returnedProduct);
    }

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c пустой ценой")
    @Description("""
                   Добавление продукта c пустой ценой
            """)

    public void createProductWithEmptyPrice(String baseUrl, String id, String categoryId, String title, String alias, String content, String oldPrice,
                                                 String status, String keywords, String description, String hit) throws IOException, JsonParseException {
        System.out.println("Создание продукта c пустой price - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, "", oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("1265 Data truncated for column 'price' at row 1"),
                "Ожидалось что добавление продукта c пустой ценоой упадет с ошибкой");
    }

    @Parameters({"baseUrl", "id", "categoryId", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c пустым title")
    @Description("""
                   Добавление продукта c пустой title
            """)

    public void createProductWithNullTitle(String baseUrl, String id, String categoryId, String alias, String content, String price, String oldPrice,
                                            String status, String keywords, String description, String hit) throws IOException, JsonParseException {
        System.out.println("Создание продукта c null title - sent request for create product");
        Product productInfo = new Product(id, categoryId, null, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("1048 Column 'title' cannot be null"),
                "Ожидалось что добавление продукта c пустым title упадет с ошибкой");
    }

    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "status", "keywords", "description", "hit"})
    @Test(testName = "Создание продукта c пустой старой ценой")
    @Description("""
                   Добавление продукта c пустой старой ценой
            """)

    public void createProductWithEmptyOldPrice(String baseUrl, String id, String categoryId, String title, String alias, String content, String price,
                                            String status, String keywords, String description, String hit) throws IOException, JsonParseException {
        System.out.println("Создание продукта c пустой old price - sent request for create product");
        Product productInfo = new Product(id, categoryId, title, alias, content, price, "", status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, PostRequestType.ADD);
        Assert.assertTrue(httpRequestProduct.requestResponse.body().contains("1265 Data truncated for column 'old_price' at row 1"),
                "Ожидалось что добавление продукта c пустой ценой упадет с ошибкой");
    }

    @AfterMethod(description = "Удаление продукта", alwaysRun = true)
    @Parameters({"baseUrl", "id"})
    private void postSettings(String Url, String id) {
        // удаление продукта
        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        System.out.println("Удаление продукта с id: " + addedId);
        httpRequestProduct.sendDeleteRequest(Url, addedId);
        assertEquals(httpRequestProduct.requestResponse.statusCode(), 200);
    }

    public static void checkProduct(Product expectedProduct, Product returnedProduct) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(returnedProduct.getId(), expectedProduct.getId(),
                "Значение id отличается от ожидаемого");
        softAssert.assertTrue(returnedProduct.getId().matches("\\d+"),
                "Значение id не число");
        softAssert.assertEquals(returnedProduct.getCategoryId(), expectedProduct.getCategoryId(),
                "Значение category id отличается от ожидаемого");
        softAssert.assertTrue(Integer.parseInt(returnedProduct.getCategoryId()) >= 1 &&
                        Integer.parseInt(returnedProduct.getCategoryId()) <= 15,
                "Значение category id принимает значение отличное от числа от 1 до 15");
        softAssert.assertEquals(returnedProduct.getTitle(), expectedProduct.getTitle(),
                "Значение title отличается от ожидаемого");
        softAssert.assertTrue(returnedProduct.getAlias().startsWith(expectedProduct.getAlias()),
                "Значение alias отличается от ожидаемого");
        softAssert.assertEquals(returnedProduct.getContent(), expectedProduct.getContent(),
                "Значение content");
        softAssert.assertEquals(returnedProduct.getPrice(), expectedProduct.getPrice(),
                "Значение price отличается от ожидаемого");
        softAssert.assertTrue(returnedProduct.getPrice().matches("\\d+(\\.\\d+)?"),
                "Значение price не число");
        softAssert.assertEquals(returnedProduct.getOldPrice(), expectedProduct.getOldPrice(),
                "Значение old_price отличается от ожидаемого");
        softAssert.assertTrue(returnedProduct.getOldPrice().matches("\\d+(\\.\\d+)?"),
                "Значение price не число");
        softAssert.assertEquals(returnedProduct.getStatus(), expectedProduct.getStatus(),
                "Значение status отличается от ожидаемого");
        softAssert.assertTrue(Integer.parseInt(returnedProduct.getStatus()) == 0 ||
                        Integer.parseInt(returnedProduct.getStatus()) == 1,
                "Значение hit отличается от ожидаемого 0 или 1");
        softAssert.assertEquals(returnedProduct.getKeywords(), expectedProduct.getKeywords(),
                "Значение keywords отличается от ожидаемого");
        softAssert.assertEquals(returnedProduct.getDescription(), expectedProduct.getDescription(),
                "Значение description отличается от ожидаемого");
        softAssert.assertEquals(returnedProduct.getHit(), expectedProduct.getHit(),
                "Значение hit отличается от ожидаемого");
        softAssert.assertTrue(Integer.parseInt(returnedProduct.getHit()) == 0 ||
                        Integer.parseInt(returnedProduct.getHit()) == 1,
                "Значение hit отличается от ожидаемого 0 или 1");
        softAssert.assertAll();
    }
}
