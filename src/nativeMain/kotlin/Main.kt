import kotlin.system.exitProcess
import kotlinx.cinterop.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.*
import platform.posix.*
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

const val SAVE_FILE_V0 = "TW_Save_Date_0.twV0"
const val SAVE_FILE_V1 = "TW_Save_Date_0.twV1"

const val GONGFA_DATA = "data/GongFa_Date.txt.json"
const val SKILL_DATA  = "data/Skill_date.txt.json"

@ImplicitReflectionSerializer
fun main() {

    var target = -1

    setlocale(LC_ALL, "zh_CN.UTF-8")

    // 初始化 data
    val workers = arrayOf(
        Worker.start().execute(TransferMode.SAFE, {  }) { DataHelper.GongFasDataHelper.data.keys },
        Worker.start().execute(TransferMode.SAFE, {  }) { DataHelper.SkillDataHelper.data.keys }
    )

    val fileName = if (access(SAVE_FILE_V1, 2) == 0) SAVE_FILE_V1 else if (access(SAVE_FILE_V0, 2) == 0) SAVE_FILE_V0 else ""

    if (fileName.isEmpty()) {
        println("当前目录下没有存档文件！")
        readLine()
        return
    }

    println("正在读取 $fileName ...")
    val inputFp = fopen(fileName, "r")
    if (inputFp == null) {
        println("读取失败！")
        readLine()
        return
    }

    val lines = arrayListOf<String>()
    memScoped {
        var tmpStr: String? = ""
        val buffer = ByteArray(2048000)
        var scan = fgets(buffer.refTo(0), buffer.size, inputFp)
        if (scan != null) {
            while (scan != null) {
//                lines.add(scan.toKString())
                tmpStr += scan.toKString()
                scan = fgets(buffer.refTo(0), buffer.size, inputFp)
            }
        }
        fclose(inputFp)
        lines.addAll(tmpStr!!.split("\n").map { "$it\n" })
        tmpStr = null
    }
    println("正在解析数据...")
    if (lines.size == 1) { // 旧版存档
        println("暂不支持旧版存档！")
        readLine()
        return
    }

    val json = Json(JsonConfiguration.Stable).parseJson(lines[1]).jsonObject
    val actorName = json["_mainActorName"]?.primitive?.content ?: "null"
    val actorId = json["_mianActorId"]?.primitive?.content ?: "null"

    val gongFaBookHelper = BooksHelper(json["_gongFaBookPages"]!!.jsonObject)
    val skillBookHelper = BooksHelper(json["_skillBookPages"]!!.jsonObject)

    lines.forEachIndexed { index, line ->
        if (line.startsWith("###+ActorCombatSkills###")) {
            target = index + 1
        }
    }
    if (target == -1 || target >= lines.size - 1) {
        println("数据有误！")
        readLine()
        return
    }
//    val workerGongfa = Worker.start().execute(TransferMode.SAFE, { Triple(Json(JsonConfiguration.Stable), lines[target], actorId) }) {
//        it.first.parseJson(it.second).jsonObject[it.third]!!.jsonObject
//    }
//
//    lateinit var t: JsonObject
//    workerGongfa.consume {
//        t = it
//    }

    val helperSkill = SkillsHelper(json.getObject("_actorSkills"), skillBookHelper)
    val helperGongFas = GongFasHelper(Json(JsonConfiguration.Stable).parseJson(lines[target]).jsonObject[actorId]!!.jsonObject, gongFaBookHelper)

    val gongFas = arrayListOf<GongFasHelper.GongFa>()
    helperGongFas.gongFas.content.keys.filter { it != "0" }.forEach {
        val gongFa = helperGongFas.get(it)
        gongFas.add(gongFa)
    }
    val skills = arrayListOf<SkillsHelper.Skill>()
    helperSkill.skills.keys.forEach {
        skills.add(helperSkill.get(it))
    }

    workers.forEach { it.consume {  } }

    println()
    println("读取成功！角色名字：$actorName 角色id：$actorId")
    println()


    val consoleHelper = ConsoleHelper()
    consoleHelper.welcome()

    while (true) {
        val cmd = consoleHelper.propmpt()
        val args = cmd?.split("\\s+".toRegex())?.dropLastWhile { it.isEmpty() } ?: arrayListOf("")
        if (args.isNotEmpty()) {
            println()
            when (args[0]) {
                "h", "?", "help" -> consoleHelper.help()
                "exit", "quit", "q" -> exitProcess(0)
                "gongfa", "g", "gf" -> {
                    if (args.size >= 2) {
                        when (args[1]) {
                            "list", "l" -> { gongFas.forEach { gf -> println(gf.toString()) } }
                            "add", "a" -> {
                                if (args.size >= 3) {
                                    val id = args[2]
                                    val percentExcrised = if (args.size >= 4) args[3].toInt() else 0
                                    val side = if (args.size >= 5) args[4] else "0"
                                    val gongFa = GongFasHelper.GongFa(id, percentExcrised, when(side) {
                                        "1" -> GongFasHelper.GongFa.Side.REVERSE
                                        "2" -> GongFasHelper.GongFa.Side.MIDDLE
                                        "3" -> GongFasHelper.GongFa.Side.OBVERSE
                                        else -> GongFasHelper.GongFa.Side.NONE
                                    })
                                    if (gongFas.find { it.id == id } != null) {
                                        gongFas.remove(gongFas.find { it.id == id }!!)
                                        if (gongFaBookHelper.exists(id)) {
                                        gongFaBookHelper.remove(id)
                                        }
                                    }
                                    gongFaBookHelper.add(BooksHelper.Book(id).apply {
                                        val pages = if (args.size >= 6) args[5].toInt() else 0
                                        percent = when(gongFa.side) {
                                            GongFasHelper.GongFa.Side.OBVERSE, GongFasHelper.GongFa.Side.REVERSE ->
                                                maxOf(6, pages)
                                            GongFasHelper.GongFa.Side.MIDDLE -> 10
                                            GongFasHelper.GongFa.Side.NONE -> 0
                                        }
                                    })
                                    gongFas.add(gongFa)
                                    println(gongFa)
                                    println("已添加")
                                } else {
                                    println("缺少 id")
                                }
                            }
                            "remove", "r", "delete", "d" -> {
                                if (args.size >= 3) {
                                    val id = args[2]
                                    val gongFa = gongFas.find { it.id == id }
                                    if (gongFa != null) {
                                        println(gongFa)
                                        gongFas.remove(gongFa)
                                        if (gongFaBookHelper.exists(id)) {
                                            gongFaBookHelper.remove(id)
                                        }
                                        println("已移除")
                                    } else {
                                        println("查无此法")
                                    }
                                } else {
                                    println("缺少 id")
                                }
                            }
                        }
                    } else {
                        println("参数不足")
                    }
                }
                "skill", "skills", "s" -> {
                    if (args.size >= 2) {
                        when (args[1]) {
                            "list", "l" -> {
                                skills.forEach { skill -> println(skill) }
                            }
                            "add", "a" -> {
                                if (args.size >= 3) {
                                    val id = args[2]
                                    val percent = if (args.size >= 4) args[3].toInt() else 0
                                    val pages = if (args.size >= 5) args[4].toInt() else 0
                                    skillBookHelper.add(BooksHelper.Book(id).apply { this.percent = pages })
                                    val targetSkill = skills.find { it.id == id }
                                    if (targetSkill != null) {
                                        skills.remove(targetSkill)
                                    }
                                    skills.add(SkillsHelper.Skill(id, percent, skillBookHelper).apply { println(this) })
                                    println("已添加")
                                } else {
                                    println("缺少 id")
                                }
                            }
                            "remove", "r", "delete", "d" -> {
                                if (args.size >= 3) {
                                    val id = args[2]
                                    val skill = skills.find { it.id == id }
                                    if (skill != null) {
                                        println(skill)
                                        skills.remove(skill)
                                        if (skillBookHelper.exists(id)) {
                                            skillBookHelper.remove(id)
                                        }
                                        println("已移除")
                                    } else {
                                        println("查无此艺")
                                    }
                                } else {
                                    println("缺少 id")
                                }
                            }
                        }
                    } else {
                        println("参数不足")
                    }
                }
                "save", "export" -> {
                    memScoped {
                        // 功法数据序列化
                        val w0 = Worker.start().execute(TransferMode.UNSAFE, { Triple(lines[target], gongFas, actorId) }) {
                                memScoped {
                                    val myJson = Json(JsonConfiguration.Stable)
                                    val gongFasJson = JsonObject(HashMap<String, JsonElement>().apply {
                                        it.second.forEach {
                                            put(it.id, it.json)
                                        }
                                        if (!containsKey("0")) {
                                            put("0", myJson.parseJson("[100,0,0]"))
                                        }
                                    })
//
                                    val oldGongFas = HashMap<String, JsonElement>().apply {
                                        putAll(myJson.parseJson(it.first).jsonObject.content)
                                    }
                                    oldGongFas[it.third] = gongFasJson
                                    JsonObject(oldGongFas)
                                }
                            }
                        // 功法心法序列化 -> _gongFaBookPages
                        val w1 = Worker.start().execute(TransferMode.UNSAFE, { gongFaBookHelper.mybooks }) {
                            memScoped {
//                                val myJson = Json(JsonConfiguration.Stable)
//                                val oldGongFaBooks = HashMap<String, JsonElement>().apply {
//                                    putAll(myJson.parseJson(it.first).jsonObject.content)
//                                }
//                                oldGongFaBooks["_gongFaBookPages"] = JsonObject(it.second)
//                                JsonObject(oldGongFaBooks)
                                JsonObject(it)
                            }
                        }
                        // 技艺数据序列化 -> _actorSkills
                        val w2 = Worker.start().execute(TransferMode.UNSAFE, { skills }) {
                            val result = HashMap<String, JsonElement>()
                            it.forEach { skill ->
                                val myJson = Json(JsonConfiguration.Stable)
                                result[skill.id] = myJson.parseJson("[${skill.percent},0]")
                            }
                            JsonObject(result)
                        }
                        // 技艺心法初始化 -> _skillBookPages
                        val w3 =  Worker.start().execute(TransferMode.UNSAFE, { skillBookHelper.mybooks }) {
                            JsonObject(it)
                        }

                        if (args[0] == "save") {
                            lines[target] = "${w0.result}\n"
                            val gongFaBookJson = w1.result
                            val skillsJson = w2.result
                            val skillBooksJson = w3.result

                            val newMap = HashMap<String, JsonElement>().apply {
                                putAll(json.content)
                                put("_gongFaBookPages", gongFaBookJson)
                                put("_actorSkills", skillsJson)
                                put("_skillBookPages", skillBooksJson)
                            }

                            lines[1] = "${JsonObject(newMap)}\n"

                            val outputFp = fopen(fileName, "w")
                            if (outputFp != null) {
                                lines.forEach {
                                    fputs(it, outputFp)
                                }
                                fclose(outputFp)
                                println("写入成功!")
                            } else {
                                println("写入失败")
                            }
                        } else {
                            val filename = if (args.size > 1) args[1] else "skills.data"
                            val fileLines = arrayOf("${(w0.result.content[actorId] ?: error("")).jsonObject}\n", "${w1.result}\n", "${w2.result}\n","${w3.result}\n")

                            val outputFp = fopen(filename, "w")
                            if (outputFp != null) {
                                fileLines.forEach {
                                    fputs(it, outputFp)
                                }
                                fclose(outputFp)
                                println("导出成功!")
                            } else {
                                println("导出失败")
                            }
                        }
                    }
                }
                "import", "i" -> {
                    val filename = if (args.size > 1) args[1] else "skills.data"
                    val fileLines = arrayListOf<String>()
                    memScoped {
                        val fp = fopen(filename, "r")
                        // skills.data 我给你个 4M 缓存怎么都不会爆了吧
                        val buffer = ByteArray(4096000)
                        var fscan = fgets(buffer.refTo(0), buffer.size, fp)
                        if (fscan != null) {
                            while (fscan != null) {
                                fileLines.add(fscan.toKString())
                                fscan = fgets(buffer.refTo(0), buffer.size, fp)
                            }
                        }
                        fclose(fp)
                    }
                    val jsonLines = fileLines.map { it.toJsonWorker() }
                    val newGongFaMap = HashMap<String, JsonElement>().apply {
                        putAll(Json(JsonConfiguration.Stable).parseJson(lines[target]).jsonObject.content)
                        put(actorId, jsonLines[0].result)
                    }
                    lines[target] = JsonObject(newGongFaMap).toString() + "\n"
                    val newMap = HashMap<String, JsonElement>().apply {
                        putAll(json.content)
                        put("_gongFaBookPages", jsonLines[1].result)
                        put("_actorSkills", jsonLines[2].result)
                        put("_skillBookPages", jsonLines[3].result)
                    }
                    lines[1] = JsonObject(newMap).toString() + "\n"

                    val outputFp = fopen(fileName, "w")
                    if (outputFp != null) {
                        lines.forEach {
                            fputs(it, outputFp)
                        }
                        println("导入成功！按回车后程序将自动退出。如需继续修改请重新运行。")
                    } else {
                        println("导入失败！请重新运行本程序。")
                    }

                    readLine()
                    exitProcess(0)
                }
                // when end
            }
        }
    }

}