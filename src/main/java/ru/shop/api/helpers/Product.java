package ru.shop.api.helpers;

public class Product {
    public String id;
    public String category_id;
    public String title;
    public String alias;
    public String content;
    public String price;
    public String old_price;
    public String status;
    public String keywords;
    public String description;
    public String hit;
    public String img;
    public String cat;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setCategoryId(String categoryId) {
        this.category_id = categoryId;
    }

    public String getCategoryId() {
        return this.category_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return this.price;
    }

    public void setOldPrice(String oldPrice) {
        this.old_price = oldPrice;
    }

    public String getOldPrice() {
        return this.old_price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getHit() {
        return this.hit;
    }

    public Product() {
    }

    public Product(String id, String category_id, String title, String alias, String content, String price, String old_price,
                   String status, String keywords, String description, String hit) {
        this.id = id;
        this.category_id = category_id;
        this.title = title;
        this.alias = (alias == null) ? "" : alias;
        this.content = content;
        this.price = price;
        this.old_price = old_price;
        this.status = status;
        this.keywords = keywords;
        this.description = description;
        this.hit = hit;
    }

    public Product(String status, String id) {
        this.status = status;
        this.id = id;
    }

    public Product findProductById(Product[] products) {
        for (Product product : products) {
            if (product.id.equals(this.id)) {
                return product;
            }
        }
        return null;
    }

}
