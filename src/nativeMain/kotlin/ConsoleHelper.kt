
class ConsoleHelper {

    fun welcome() {
        printlnChs("输入 ? 或 h 获取帮助")
    }

    fun help() {
        printlnChs("可使用的命令:")
        printlnChs()
        printlnChs("help")
        printlnChs("    显示本界面")
        printlnChs("gongfa list")
        printlnChs("    显示当前已修炼的功法列表")
        printlnChs()
        printlnChs("gongfa add <id> [percent] [arg] [pages]")
        printlnChs("    添加一门功法到太吾的功法列表，如已存在则原功法会被移除再重新添加")
        printlnChs("        id:      功法的 id")
        printlnChs("        percent: 修炼进度，可选，默认为0")
        printlnChs("        arg:     功法书籍的修炼情况")
        printlnChs("                 0: 未阅读")
        printlnChs("                 1: 逆练")
        printlnChs("                 2: 功法冲解")
        printlnChs("                 3: 正练")
        printlnChs("        pages:   功法书籍已阅读的章节，从第一页开始算。arg 参数为1或3的时候默认6章，为2的时候默认10章且不可更改")
        printlnChs("    示例：")
        printlnChs("        gongfa add 70902")
        printlnChs("            学习工布独一剑")
        printlnChs("        gongfa add 70902 100 3 7")
        printlnChs("            学习工布独一剑，效果为正练，前7章已阅读")
        printlnChs()
        printlnChs("gongfa remove <id>")
        printlnChs("    遗忘一门功法并把书籍设定为未阅读的状态")
        printlnChs("    示例：")
        printlnChs("        gongfa remove 70902")
        printlnChs("            遗忘工布独一剑")
        printlnChs()
        printlnChs("skiils list")
        printlnChs("    显示当前已修炼的技艺列表")
        printlnChs()
        printlnChs("skills add <id> [percent] [pages]")
        printlnChs("    添加一门技艺到太吾的功法列表，如已存在则原技艺会被移除再重新添加")
        printlnChs("        id:      技艺的 id")
        printlnChs("        epercent:已修炼的进度，0-100，默认为0")
        printlnChs("        pages:   技艺书籍已阅读的章节，从第一页开始算。")
        printlnChs()
        printlnChs("save")
        printlnChs("    写入修改到存档文件")

    }

    fun propmpt(): String? {
        printlnChs()
        print(">")
        return readLine()
    }

}