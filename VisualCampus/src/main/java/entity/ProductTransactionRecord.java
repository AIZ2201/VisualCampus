package entity;

public class ProductTransactionRecord {
    int productID;
    String name;
    double productPrice;
    int productAmount;
    int cardNumber;
    String time;
    String remark;
    Product item;

    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public int getProductAmount() {
        return productAmount;
    }
    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }
    public int getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Product getItem() {
        return item;
    }
    public void setItem(Product item) {
        this.item = item;
    }
}
