package entity;


import java.util.List;

public class TeachingClass {
    private Integer id;
    private Integer cardNumber;
    private String courseID;
    private String courseName;
    private Double credit;
    private String teacherName;
    private String classroomName;
    private Integer capacity;
    private Integer selectCount;
    private String weekRange;
    private List<String> dayOfWeek;
    private List<Integer> courseBegin;
    private List<Integer> courseEnd;
    private Boolean isSelected;
    public TeachingClass() {}
    public TeachingClass(Integer id,Integer cardNumber,String courseID,String courseName,Double credit,String teacherName,
                         String classroomName,Integer capacity,Integer selectCount,String weekRange,List<String> dayOfWeek,List<Integer> courseBegin,List<Integer> courseEnd,Boolean isSelected) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.courseID = courseID;
        this.courseName = courseName;
        this.credit = credit;
        this.teacherName = teacherName;
        this.classroomName = classroomName;
        this.capacity = capacity;
        this.selectCount = selectCount;
        this.weekRange = weekRange;
        this.dayOfWeek = dayOfWeek;
        this.courseBegin = courseBegin;
        this.courseEnd = courseEnd;
        this.isSelected = isSelected;
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

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(Integer selectCount) {
        this.selectCount = selectCount;
    }

    public String getWeekRange() {
        return weekRange;
    }

    public void setWeekRange(String weekRange) {
        this.weekRange = weekRange;
    }

    public List<String> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(List<String> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Integer> getCourseBegin() {
        return courseBegin;
    }

    public void setCourseBegin(List<Integer> courseBegin) {
        this.courseBegin = courseBegin;
    }

    public List<Integer> getCourseEnd() {
        return courseEnd;
    }

    public void setCourseEnd(List<Integer> courseEnd) {
        this.courseEnd = courseEnd;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
