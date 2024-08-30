package com.output;


public class User {
    int cardNumber;
    String operation;
    //String name;
    String password;
    String searchText;
    Student student;
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public int getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }
    /*public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }*/

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setSearchText(String searchText) { this.searchText = searchText; }
    public String getSearchText() { return this.searchText; }
    public void setStudent(Student student) { this.student = student; }
    public Student getStudent() { return student; }
}

