package entity;

import java.util.List;

public class EvaluationOfTeaching {
    private Integer id;
    private Integer cardNumber;
    private String courseName;
    private String teacherName;
    private List<Integer> scores;
    private Boolean isEvaluated;
    public EvaluationOfTeaching() {}
    public EvaluationOfTeaching(Integer id, Integer cardNumber, String courseName, String teacherName,List<Integer> scores, Boolean isEvaluated) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.scores = scores;
        this.isEvaluated = isEvaluated;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public Boolean getEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(Boolean evaluated) {
        isEvaluated = evaluated;
    }
}

