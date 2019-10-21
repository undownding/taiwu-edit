import kotlinx.serialization.json.*


class GongFasHelper(val gongFas: JsonObject, private val booksHelper: BooksHelper) {

    fun get(id: String) = GongFa(id, gongFas[id]!!.jsonArray, booksHelper)

    fun exists(id: String) = gongFas.containsKey(id)
//
//    fun remove(id: String) = gongFas.remove(id)
//
//    fun add(gongFa: GongFa) = gongFas.add(gongFa.id, gongFa.data)

    val size get() = gongFas.keys.size

    data class GongFa(val id: String, val percent: Int, val side: Side) {

        constructor(
            id: String, data: JsonArray, booksHelper: BooksHelper,
            book: BooksHelper.Book = booksHelper.get(id), rev: Int = data[2].primitive.int
        ): this(id, data[0].primitive.int,
            when {
                book.percent == 10 && rev == 5 -> Side.MIDDLE
                book.percent  - rev > 5  -> Side.OBVERSE
                book.percent > 5 && rev > 5  -> Side.REVERSE
                else -> Side.NONE
            }
        ) {
//            println()
        }

        private val data get() = when (side) {
            Side.OBVERSE -> arrayOf(percent, 0, 0)
            Side.MIDDLE  -> arrayOf(percent, 0, 5)
            Side.REVERSE -> arrayOf(percent, 0, 10)
            Side.NONE    -> arrayOf(percent, 0, 0
            )
        }

        val json get() = Json(JsonConfiguration.Stable).parseJson("[${data[0]},${data[1]},${data[2]}]")

        override fun toString(): String = "${(DataHelper.GongFasDataHelper.get(id) + "      ").substring(0, 6)}\t修炼进度: $percent\t${when(side) {
            Side.OBVERSE -> "心法正练"
            Side.MIDDLE -> "心法冲解"
            Side.REVERSE -> "心法逆练"
            Side.NONE -> "未出心法效果"
        }}"

        /**
         * 记录该功法的冲解情况
         *
         * @property OBVERSE 正练
         * @property MIDDLE 冲解
         * @property REVERSE 逆练
         * @property NONE 未解读
         */
        enum class Side {
            OBVERSE,
            MIDDLE,
            REVERSE,
            NONE
        }
    }

}