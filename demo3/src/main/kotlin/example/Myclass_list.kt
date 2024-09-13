package example
import example.handlers.class_info
object Myclass_list {
    private var classInfo: List<example.handlers.class_info> = emptyList()

    fun updateClassInfo(new_ClassInfo: List<class_info>) {
        classInfo = new_ClassInfo
    }

    fun getClassInfo(): List<example.handlers.class_info> {
        return classInfo
    }
}