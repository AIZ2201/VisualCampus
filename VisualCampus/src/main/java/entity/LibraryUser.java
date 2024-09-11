package entity;


import java.util.LinkedList;

enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED // 根据实际情况定义用户状态
}

public class LibraryUser {
    private Integer userId; // 一卡通号
    private String userName; // 姓名
    private Integer userBorrowedNum; // 已借数量
    private Integer userMaxBorrowNum; // 总可借数量
    private String userStatus; // 用户状态
    private LinkedList<LibraryTran> userBorrowed; // 已借图书列表

    // 构造函数
//    public LibraryUser(Integer userId, String userName, Integer userBorrowedNum, Integer userMaxBorrowNum, UserStatus userStatus) {
//        this.userId = userId;
//        this.userName = userName;
//        this.userBorrowedNum = userBorrowedNum;
//        this.userMaxBorrowNum = userMaxBorrowNum;
//        this.userStatus = userStatus;
//        this.userBorrowed = new LinkedList<>();
//    }

    // Getter 和 Setter 方法
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserBorrowedNum() {
        return userBorrowedNum;
    }

    public void setUserBorrowedNum(Integer userBorrowedNum) {
        this.userBorrowedNum = userBorrowedNum;
    }

    public Integer getUserMaxBorrowNum() {
        return userMaxBorrowNum;
    }

    public void setUserMaxBorrowNum(Integer userMaxBorrowNum) {
        this.userMaxBorrowNum = userMaxBorrowNum;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public LinkedList<LibraryTran> getUserBorrowed() {
        return userBorrowed;
    }

    public void setUserBorrowed(LinkedList<LibraryTran> userBorrowed) {
        this.userBorrowed = userBorrowed;
    }

    // 方法可以根据需要添加
}
