package entity;

import java.util.Date;

public class LibraryCom {
    private String comId;        // 书评唯一标识符
    private Date comTime;      // 评价时间
    private Integer comUserId; // 评价人一卡通号，9位数字
    private String comUserName;// 评价人姓名
    private String comText;    // 评价内容
    private String comBookIsbn;    // 评价图书唯一标识符

    // 构造方法
    public LibraryCom(String comId, Date comTime, Integer comUserId, String comUserName, String comText, String comBookId) {
        this.comId = comId;
        this.comTime = comTime;
        this.comUserId = comUserId;
        this.comUserName = comUserName;
        this.comText = comText;
        this.comBookIsbn = comBookId;
    }

    // 无参构造方法
    public LibraryCom() {}

    // Getter 和 Setter 方法
    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public Date getComTime() {
        return comTime;
    }

    public void setComTime(Date comTime) {
        this.comTime = comTime;
    }

    public Integer getComUserId() {
        return comUserId;
    }

    public void setComUserId(Integer comUserId) {
        this.comUserId = comUserId;
    }

    public String getComUserName() {
        return comUserName;
    }

    public void setComUserName(String comUserName) {
        this.comUserName = comUserName;
    }

    public String getComText() {
        return comText;
    }

    public void setComText(String comText) {
        this.comText = comText;
    }

    public String getComBookIsbn() {
        return comBookIsbn;
    }

    public void setComBookIsbn(String comBookIsbn) {
        this.comBookIsbn = comBookIsbn;
    }


}
