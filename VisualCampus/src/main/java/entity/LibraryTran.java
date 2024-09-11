package entity;

import java.util.Date;

public class LibraryTran {
    private String tranId; // 借阅信息编号
    private Date tranBorrowTime; // 借阅时间
    private Date tranDueTime; // 到期时间
    private String tranBookId; // 所借书籍的唯一标识符// 所借书籍对象
    private Integer tranUserId; // 借阅人一卡通号

    // 构造函数
//    public LibraryTran(String tranId, Date tranBorrowTime, Date tranDueTime, String tranBookId, LibraryBook tranBook, Integer tranUserId) {
//        this.tranId = tranId;
//        this.tranBorrowTime = tranBorrowTime;
//        this.tranDueTime = tranDueTime;
//        this.tranBookId = tranBookId;
//        this.tranBook = tranBook;
//        this.tranUserId = tranUserId;
//    }

    // Getter 和 Setter 方法
    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public Date getTranBorrowTime() {
        return tranBorrowTime;
    }

    public void setTranBorrowTime(Date tranBorrowTime) {
        this.tranBorrowTime = tranBorrowTime;
    }

    public Date getTranDueTime() {
        return tranDueTime;
    }

    public void setTranDueTime(Date tranDueTime) {
        this.tranDueTime = tranDueTime;
    }

    public String getTranBookId() {
        return tranBookId;
    }

    public void setTranBookId(String tranBookId) {
        this.tranBookId = tranBookId;
    }


    public Integer getTranUserId() {
        return tranUserId;
    }

    public void setTranUserId(Integer tranUserId) {
        this.tranUserId = tranUserId;
    }

    // 方法可以根据需要添加
}