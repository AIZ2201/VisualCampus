package entity;


import java.util.List;

public class StudentSelectClass {
    private Integer id;
    private Integer cardNumber;
    private String courseName;
    private String teacherName;
    private Integer qqGroup;
    private String classroomName;
    private String duration;
    private List<Integer> week;
    private List<String> time;
    private String introduction;
    public StudentSelectClass() {}
    public StudentSelectClass(Integer id,Integer cardNumber,String courseName,String teacherName,
                              Integer qqGroup,String classroomName,String duration,List<Integer> week,List<String> time,String introduction) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.qqGroup = qqGroup;
        this.classroomName = classroomName;
        this.duration = duration;
        this.week = week;
        this.time = time;
        this.introduction = introduction;
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

    public Integer getQqGroup() {
        return qqGroup;
    }

    public void setQqGroup(Integer qqGroup) {
        this.qqGroup = qqGroup;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getTime() {
        return time;
    }

    public void setTime(List<String> time) {
        this.time = time;
    }

    public List<Integer> getWeek() {
        return week;
    }

    public void setWeek(List<Integer> week) {
        this.week = week;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}

