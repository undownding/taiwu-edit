import kotlinx.cinterop.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import platform.posix.memset
import platform.posix.printf
import platform.posix.wmemset
import platform.windows.*
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

fun String.toJsonWorker() = Worker.start().execute(TransferMode.UNSAFE, { this }) { Json(JsonConfiguration.Stable).parseJson(it).jsonObject }

//fun String.toUnicode()  {
//    this.toCharArray().map {
//        val bytes = it.toString().encodeToByteArray()
//        when (bytes.size) {
//            1 -> it.toString()
//            2 -> {
//                val high = bytes[0].toInt()
//                val low  = bytes[1]
//                if ((high and 0xe0) == 0xc0) {
//                    ByteArray(2).apply {
//                        this[0] = ((high shl  6) + (low and 0x3f.toByte())).toByte()
//                        this[1] = ((high shr 2) and 0x07).toByte()
//                    }.toKString()
//                } else {
//                    it.toString()
//                }
//            }
//            3 -> {
//                val high = bytes[0]
//                val middle = bytes[1]
//                val low = bytes[2]
//            }
//            else -> it.toString()
//        }
//    }
//}

fun String.utf8ToGbk(): CArrayPointer<ByteVarOf<Byte>> {
    var len = MultiByteToWideChar(CP_UTF8, 0, this, -1, null, 0)
    val unicode = nativeHeap.allocArray<UShortVarOf<UShort>>(len)
    wmemset(unicode, 0, len.toULong())
    MultiByteToWideChar(CP_UTF8, 0, this, -1, unicode, len)

    len = WideCharToMultiByte(CP_ACP, 0, unicode, -1, null, 0, null, null)
    val result = nativeHeap.allocArray<ByteVarOf<Byte>>(len + 1)
    memset(result, 0, (len + 1).toULong())
    WideCharToMultiByte(CP_ACP, 0, unicode, -1, result, len, null, null)
    return result
}

fun printlnChs() {
    println()
}

fun printlnChs(message: Any) = printlnChs(message.toString())

fun printlnChs(str: String) {
    printf("%s\n", str.utf8ToGbk())
}