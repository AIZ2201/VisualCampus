package example
import example.Course

object Mygrade_list {
    private var courses: List<example.handlers.Course> = emptyList()

    fun updateCourses(newCourses: List<example.handlers.Course>) {
        courses = newCourses
    }

    fun getCourses(): List<example.handlers.Course> {
        return courses
    }
}



