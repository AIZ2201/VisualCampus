package example

import net.sf.json.JSONObject

// 定义实体类 Person
data class Person(
    val name: String,
    val age: Int,
    val city: String
)

// 将实体类 Person 转换为 JSON 对象的函数
fun personToJson(person: Person): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("name", person.name)
    jsonObject.put("age", person.age)
    jsonObject.put("city", person.city)
    return jsonObject
}

// 使用 json-lib 库的 fromObject 方法自动转换实体类为 JSON 对象
fun personToJsonUsingLib(person: Person): JSONObject {
    return JSONObject.fromObject(person)
}

fun main() {
    // 创建一个 Person 对象
    val person = Person(name = "John Doe", age = 30, city = "New York")

    // 将 Person 对象转换为 JSON 对象（手动构建）
    val jsonObjectManual = personToJson(person)
    println("手动构建 JSON 对象:")
    println(jsonObjectManual.toString(2)) // 美化打印 JSON 对象

    // 使用 json-lib 的 fromObject 方法自动转换为 JSON 对象
    val jsonObjectAuto = personToJsonUsingLib(person)
    println("\n使用 fromObject 方法自动构建 JSON 对象:")
    println(jsonObjectAuto.toString(2)) // 美化打印 JSON 对象
}
