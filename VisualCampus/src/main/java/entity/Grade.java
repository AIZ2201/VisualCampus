package entity;
public class Grade {
    private Integer id;
    //一卡通号
    private Integer cardNumber;
    //课程名
    private String courseName;
    //学分
    private Double credit;
    //总成绩
    private Integer grade;
    //平时分
    private Integer regular_grade;
    //期中成绩
    private Integer midterm_grade;
    //期末成绩
    private Integer final_grade;
    public Grade() {}
    public Grade(Integer id,Integer cardNumber,String courseName, double credit, Integer grade, Integer regular_grade, Integer midterm_grade, Integer final_grade) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.courseName = courseName;
        this.credit = credit;
        this.grade = grade;
        this.regular_grade = regular_grade;
        this.midterm_grade = midterm_grade;
        this.final_grade = final_grade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(Integer cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getRegular_grade() {
        return regular_grade;
    }

    public void setRegular_grade(Integer regular_grade) {
        this.regular_grade = regular_grade;
    }

    public Integer getMidterm_grade() {
        return midterm_grade;
    }

    public void setMidterm_grade(Integer midterm_grade) {
        this.midterm_grade = midterm_grade;
    }

    public Integer getFinal_grade() {
        return final_grade;
    }

    public void setFinal_grade(Integer final_grade) {
        this.final_grade = final_grade;
    }
}

