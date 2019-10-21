import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker

fun String.toJsonWorker() = Worker.start().execute(TransferMode.UNSAFE, { this }) { Json(JsonConfiguration.Stable).parseJson(it).jsonObject }