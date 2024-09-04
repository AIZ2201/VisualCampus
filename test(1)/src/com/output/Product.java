package com.output;

public class Product {
    int productID;
    String name;
    double price;
    String pictureLink;
    int stock;
    int sales;
    String description;

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getPictureLink() {
        return pictureLink;
    }
    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getSales() {
        return sales;
    }
    public void setSales(int sales) {
        this.sales = sales;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
