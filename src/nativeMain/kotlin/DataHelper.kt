import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonObject
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import kotlin.system.exitProcess

abstract class DataHelper(filename: String) {

    val data: JsonObject

    init {
        val inputFp = fopen(filename, "r")
        if (inputFp == null) {
            println("功法文件不存在！")
            readLine()
            exitProcess(0)
        }

        val lines = arrayListOf<String>()
        val buffer = ByteArray(2048000)
        var scan = fgets(buffer.refTo(0), buffer.size, inputFp)
        if (scan != null) {
            while (scan != null) {
                lines.add(scan.toKString())
                scan = fgets(buffer.refTo(0), buffer.size, inputFp)
            }
        }
        fclose(inputFp)

        data = Json(JsonConfiguration.Stable).parseJson(lines[0]).jsonObject
    }

    open fun get(id: String) =
        data[id]?.jsonObject?.get("0")?.primitive?.content ?:""


    object GongFasDataHelper: DataHelper(GONGFA_DATA) {
        override fun get(id: String) = if (super.get(id).isEmpty()) "未知功法" else super.get(id)
    }

    object SkillDataHelper: DataHelper(SKILL_DATA) {
        override fun get(id: String) = if (super.get(id).isEmpty()) "未知技艺" else super.get(id)
    }
}