package entity;

import java.util.LinkedList;

public class LibraryBook {
    String bookId;
    String bookName;
    String bookIsbn;
    String bookAuthor;
    String bookPress;
    String bookDescription;
    String bookPlace;
    String bookStatus;

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }

    int bookNum;
    private LinkedList<LibraryCom> bookComment;


    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookPress() {
        return bookPress;
    }

    public void setBookPress(String bookPress) {
        this.bookPress = bookPress;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getBookPlace() {
        return bookPlace;
    }

    public void setBookPlace(String bookPlace) {
        this.bookPlace = bookPlace;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public void setBookComment(LinkedList<LibraryCom> bookCommand) {
        if (bookCommand == null) {
            this.bookComment = new LinkedList<>(); // 如果传入的列表为空，初始化一个新的空列表
        } else {
            this.bookComment = new LinkedList<>(bookCommand); // 否则，将传入的列表赋值给 bookCommand
        }
    }

    // 获取书评列表的方法
    public LinkedList<LibraryCom> getBookComment() {
        return this.bookComment;
    }

}
