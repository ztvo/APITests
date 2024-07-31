package ru.shop.api.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class HttpRequestProduct {

    final String URL_ADD_PRODUCT = "/api/addproduct";
    final String URL_EDIT_PRODUCT = "/api/editproduct";
    final String URL_DELETE_PRODUCT = "/api/deleteproduct?id=";
    final String URL_GET_PRODUCT = "/api/products";

    public HttpResponse<String> requestResponse;
    public enum PostRequestType {
        EDIT,
        ADD;
    }

    public void sendPostRequest(String URL, Product productInfo, PostRequestType type) {
        var values = new HashMap<String, String>() {{
            put("id", productInfo.id);
            put("category_id", productInfo.category_id);
            put("title", productInfo.title);
            put("alias", productInfo.alias);
            put("content", productInfo.content);
            put("price", productInfo.price);
            put("old_price", productInfo.old_price);
            put("status", productInfo.status);
            put("keywords", productInfo.keywords);
            put("description", productInfo.description);
            put("hit", productInfo.hit);
        }};

        try {
            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(values);
            HttpClient client = HttpClient.newHttpClient();
            String urlType = type == PostRequestType.ADD? URL_ADD_PRODUCT :  URL_EDIT_PRODUCT;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + urlType))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            this.requestResponse  = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(this.requestResponse.body());
            System.out.println(this.requestResponse.statusCode());
        } catch (Exception e) {
            System.out.print("Sent post request failed: " + e);
        }
    }

    public void sendGetRequest(String URL) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + URL_GET_PRODUCT))
                    .GET()
                    .build();
            this.requestResponse  = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(this.requestResponse.body());
//            System.out.println(this.requestResponse.statusCode());
        } catch (Exception e) {
            System.out.print("Sent get request failed: " + e);
        }
    }

    public void sendDeleteRequest(String URL, String id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + URL_DELETE_PRODUCT + id))
                    .DELETE()
                    .build();
            this.requestResponse  = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(this.requestResponse.body());
            System.out.println(this.requestResponse.statusCode());
        } catch (Exception e) {
            System.out.print("Sent delete request failed: " + e);
        }
    }
}

