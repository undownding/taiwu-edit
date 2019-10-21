import kotlinx.serialization.json.JsonObject

class SkillsHelper(val skills: JsonObject, private val booksHelper: BooksHelper) {
    fun get(id: String) = Skill(id, skills[id]!!.jsonArray[0].primitive.int, booksHelper)

    fun exists(id: String) = skills.containsKey(id)

    class Skill(val id: String, val percent: Int, val booksHelper: BooksHelper) {

        override fun toString() = "${(DataHelper.SkillDataHelper.get(id) + "      ").substring(0, 6)}\t修炼进度：$percent\t阅读进度：${booksHelper.get(id).percent}"

    }
}