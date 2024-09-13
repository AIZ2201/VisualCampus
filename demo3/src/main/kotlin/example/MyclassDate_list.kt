package example
import example.class_date

object MyclassDate_list {
    private var classdate: List<example.handlers.class_date> = emptyList()

    fun updateDate(new_Classdate: List<example.handlers.class_date>) {
        classdate = new_Classdate
    }

    fun getDate(): List<example.handlers.class_date> {
        return classdate
    }
}