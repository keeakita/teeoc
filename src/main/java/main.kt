import dog.librewulf.teeoc.SteamLocator
import dog.librewulf.teeoc.Swapper
import mu.KotlinLogging
import java.nio.file.Paths

// TODO: need to do case resolution here for OS X and Linux
private val pathFromSteamRoot = "steamapps/common/The Jackbox Party Pack 3/games/AwShirt/" +
    "TalkshowExport/project/actions/5696.swf"

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val steamLocation = SteamLocator.steamPath()
    logger.info { steamLocation }

    if (steamLocation == null) return

    val swfLocation = Paths.get(steamLocation, pathFromSteamRoot).toString()
    logger.info { swfLocation }

    val res = Swapper.replaceImageAsset(
        swfLocation,
        "/tmp/out.swf",
        496,
        "/home/william/Pictures/Furry/Commissions/libreacbadge.png"
    )
    logger.info {"Result: $res" }
}
