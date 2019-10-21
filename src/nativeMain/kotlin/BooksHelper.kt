import kotlinx.coroutines.id
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.*


class BooksHelper(books: JsonObject) {
    var mybooks = HashMap<String, JsonElement>().apply { putAll(books.content) }
    
    fun get(id: String) = if (mybooks.containsKey(id)) Book(id, mybooks[id]!!.jsonArray) else Book("0")

    fun exists(id: String) = mybooks.containsKey(id)

    fun remove(id: String) {
        mybooks.remove(id)
    }

    @ImplicitReflectionSerializer
    fun add(book: Book) {
        var tmpStr = "["
        book.data.forEach {
            tmpStr += "$it,"
        }
        tmpStr = tmpStr.substring(0, tmpStr.length - 1) + "]"
        if (exists(book.id)) {
            remove(book.id)
        }
        mybooks[book.id] = Json(JsonConfiguration.Stable).parseJson(tmpStr)
    }

    data class Book(val id: String) {

        constructor(id: String, book: JsonArray): this(id) {
            data.forEachIndexed { index, _ ->
                data[index] = book[index].content.toInt()
            }
        }

        val data: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        var percent: Int = 0
            get() {
                var sum = 0
                data.forEach {
                    sum += it
                }
                return sum
            }
            set(value) {
                data.forEachIndexed { index, _ ->
                    data[index] = if (index < value ) 1 else 0
                }
                field = value
            }
    }
}