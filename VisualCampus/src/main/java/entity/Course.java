package entity;

public class Course {
    private Integer id;
    private Integer cardNumber;
    private String courseName;
    private String classroomName;
    private String classDate;
    private Integer courseBegin;
    private Integer courseEnd;
    public Course() {}
    public Course(Integer id, Integer cardNumber, String courseName, String classroomName, String classDate,Integer courseBegin,Integer courseEnd) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.courseName = courseName;
        this.classroomName = classroomName;
        this.classDate = classDate;
        this.courseBegin = courseBegin;
        this.courseEnd = courseEnd;
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

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public Integer getCourseEnd() {
        return courseEnd;
    }

    public void setCourseEnd(Integer courseEnd) {
        this.courseEnd = courseEnd;
    }

    public Integer getCourseBegin() {
        return courseBegin;
    }

    public void setCourseBegin(Integer courseBegin) {
        this.courseBegin = courseBegin;
    }
}
