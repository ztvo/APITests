package ru.shop.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.shop.api.helpers.HttpRequestProduct;
import ru.shop.api.helpers.Product;

import java.io.IOException;

public class deleteProduct {

    String addedId;

    @BeforeClass
    @Parameters({"baseUrl", "id", "categoryId", "title", "alias", "content", "price", "oldPrice", "status", "keywords", "description", "hit"})
    public void preSettings(String baseUrl, String id, String categoryId, String title, String alias, String content,
                            String price, String oldPrice, String status, String keywords, String description,
                            String hit) throws IOException {
        Product productInfo = new Product(id, categoryId, title, alias, content, price, oldPrice, status, keywords,
                description, hit);

        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendPostRequest(baseUrl, productInfo, HttpRequestProduct.PostRequestType.ADD);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200, "Запрос на добавление упал с ошибкой");

        // парсинг response
        ObjectMapper mapper = new ObjectMapper();
        Product resultProduct = mapper.readValue(httpRequestProduct.requestResponse.body(), Product.class);
        productInfo.setId(resultProduct.getId());
        addedId = resultProduct.getId();
    }

    @Test(testName = "Удаление продукта")
    @Parameters({"baseUrl"})
    @Description("""
                    Удаление продукта
                    Проверка удаления
            """)

    public void deleteProduct(String baseUrl) throws IOException {
        System.out.println("Удаление продукта - sent request to delete product");

        // удаление продукта
        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendDeleteRequest(baseUrl, addedId);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на удаление не соотвествует ожидаемому и должен быть 200");

        // Получение данных о продукте
        httpRequestProduct.sendGetRequest(baseUrl);
        Assert.assertEquals(httpRequestProduct.requestResponse.statusCode(), 200,
                "Статус запроса на получение всех продуктов не соотвествует ожидаемому и должен быть 200");

        ObjectMapper mapper = new ObjectMapper();
        Product[] productArray = mapper.readValue(httpRequestProduct.requestResponse.body(), Product[].class);
        Product returnedProduct = null;
        for (Product productItem : productArray) {
            if (productItem.id.equals(addedId)) {
                returnedProduct = productItem;
                break;
            }
        }
        Assert.assertNull(returnedProduct, "Продукт не был удален");
    }

    @Test(testName = "Удаление несуществующего продукта")
    @Parameters({"baseUrl", "unexistedId"})
    @Description("""
                    Удаление продукта по несуществующему id
            """)

    public void deleteProductByUnexistedId(String baseUrl, String id) throws IOException {
        System.out.println("Удаление несуществуюшего продукта - sent request to delete product");

        // удаление продукта
        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendDeleteRequest(baseUrl, id);
        Assert.assertTrue(httpRequestProduct.requestResponse.statusCode() == 200,
                "Ожидается 200 статус при удалении несуществующего продукта");
    }

    @Test(testName = "Удаление продукта по невалидному id")
    @Parameters({"baseUrl", "invalidId"})
    @Description("""
                    Удаление продукта по невалидному id
            """)

    public void deleteProductByInvalidId(String baseUrl, String id) throws IOException {
        System.out.println("Удаление продукта по невалидному id - sent request to delete product");

        // удаление продукта
        HttpRequestProduct httpRequestProduct = new HttpRequestProduct();
        httpRequestProduct.sendDeleteRequest(baseUrl, id);
        Assert.assertTrue(httpRequestProduct.requestResponse.statusCode() == 200,
                "Ожидается 200 статус при удалении продукта по id в нечисловом формате");
    }
}
