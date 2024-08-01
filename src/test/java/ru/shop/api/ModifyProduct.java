package ru.shop.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.shop.api.helpers.HttpRequestProduct;
import ru.shop.api.helpers.Product;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

public class ModifyProduct {
    String addedId;

    @BeforeMethod
    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    public void preSettings(String baseUrl, String id, String categoryId, String title, String alias, String content, String price, String oldPrice,
                            String status, String keywords, String description, String hit) throws IOException {
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, HttpRequestProduct.PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200, "Запрос на добавление упал с ошибкой");

        // парсинг респонза
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();
    }

    @Parameters({"baseUrl", "categoryId", "newTitle", "alias", "content", "newPrice", "oldPrice", "status", "keywords", "description", "hit"})
    @Test(testName = "Изменение продукта")
    @Description("""
                    1. Изменение продукта: title и price 
                    2. Получение данных о продукте
                    3. Проверка данных о продукте
            """)

    public void modifyProduct(String baseUrl, String categoryId, String title, String alias, String content, String price, String oldPrice,
                              String status, String keywords, String description, String hit) throws IOException {
        System.out.println("Изменение продукта - sent request for modifying product");
        Product productInfo = new Product(addedId, categoryId, title, alias, content, price, oldPrice, status, keywords, description,
                hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, HttpRequestProduct.PostRequestType.EDIT);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Запрос на изменение упал с ошибкой");

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Запрос на получение продуктов упал с ошибкой");
        ObjectMapper mapper = new ObjectMapper();
        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = productInfo.findProductById(productArray);

        // проверка полей
        CreateProductTest.checkProduct(productInfo, returnedProduct);
    }

    @AfterMethod(description = "Удаление продукта", alwaysRun = true)
    @Parameters({"baseUrl", "id"})
    private void postSettings(String Url, String id) {
        // удаление продукта
        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendDeleteRequest(Url, addedId);
        assertEquals(httpRequestProduct.requestResponse.statusCode(), 200);
    }
}
