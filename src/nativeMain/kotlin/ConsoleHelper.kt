
class ConsoleHelper {

    fun welcome() {
        println("输入 ? 或 h 获取帮助")
    }

    fun help() {
        println("可使用的命令:")
        println()
        println("help")
        println("    显示本界面")
        println("gongfa list")
        println("    显示当前已修炼的功法列表")
        println()
        println("gongfa add <id> [percent] [arg] [pages]")
        println("    添加一门功法到太吾的功法列表，如已存在则原功法会被移除再重新添加")
        println("        id:      功法的 id")
        println("        percent: 修炼进度，可选，默认为0")
        println("        arg:     功法书籍的修炼情况")
        println("                 0: 未阅读")
        println("                 1: 逆练")
        println("                 2: 功法冲解")
        println("                 3: 正练")
        println("        pages:   功法书籍已阅读的章节，从第一页开始算。arg 参数为1或3的时候默认6章，为2的时候默认10章且不可更改")
        println("    示例：")
        println("        gongfa add 70902")
        println("            学习工布独一剑")
        println("        gongfa add 70902 100 3 7")
        println("            学习工布独一剑，效果为正练，前7章已阅读")
        println()
        println("gongfa remove <id>")
        println("    遗忘一门功法并把书籍设定为未阅读的状态")
        println("    示例：")
        println("        gongfa remove 70902")
        println("            遗忘工布独一剑")
        println()
        println("skiils list")
        println("    显示当前已修炼的技艺列表")
        println()
        println("skills add <id> [percent] [pages]")
        println("    添加一门技艺到太吾的功法列表，如已存在则原技艺会被移除再重新添加")
        println("        id:      技艺的 id")
        println("        epercent:已修炼的进度，0-100，默认为0")
        println("        pages:   技艺书籍已阅读的章节，从第一页开始算。")
        println()
        println("save")
        println("    写入修改到存档文件")

    }

    fun propmpt(): String? {
        println()
        print(">")
        return readLine()
    }

}