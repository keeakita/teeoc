import dog.librewulf.teeoc.SteamLocator
import dog.librewulf.teeoc.Swapper
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

// TODO: need to do case resolution here for OS X and Linux
private val pathFromSteamRoot = "steamapps/common/"

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val steamLocation = SteamLocator.steamPath()
    logger.info { "Steam base path: ${steamLocation}" }

    if (steamLocation == null) return

    val basePath = Paths.get(steamLocation, pathFromSteamRoot).toString()

    val perSwfInfo: Map<String, SWFAssetInfo> = YAMLReader.read("teeko_ids.yaml")
    val loginPhase = perSwfInfo["character_select"]

    if (loginPhase == null) {
        logger.error { "Couldn't find data for the login phase" }
        exitProcess(255)
    }

    val targetSWF = Paths.get(basePath, loginPhase.file).toString()
    logger.info { "SWF file path: ${targetSWF}" }

    val dogVictory = loginPhase.visual_assets?.get("character_select_icons")
            ?.get(TeeKOCharacterName.DOG.str)

    val redVictory = loginPhase.visual_assets?.get("character_select_icons")
            ?.get(TeeKOCharacterName.RED_DEMON.str)

    val catVictory = loginPhase.visual_assets?.get("character_select_icons")
            ?.get(TeeKOCharacterName.CAT.str)

    if (dogVictory == null || redVictory == null || catVictory == null) {
        logger.error { "Couldn't find the right ID for character icons" }
        exitProcess(255)
    }

    // Backup the original file to be safe, and always use the original as the source
    val origPathObj = Paths.get(targetSWF + ".orig")
    if (!Files.exists(origPathObj)) {
        Files.copy(Paths.get(targetSWF), origPathObj)
    }

    // Files.delete(targetSWF)

    var res = Swapper.replaceImageAsset(
        origPathObj.toString(),
        targetSWF,
        dogVictory,
        "/home/william/tmp/libre_icon.png"
    )
    logger.info {"Result: $res" }

    res = Swapper.replaceImageAsset(
            targetSWF.toString(),
            targetSWF,
            redVictory,
            "/home/william/tmp/hours_icon.png"
    )
    logger.info {"Result: $res" }

    res = Swapper.replaceImageAsset(
            targetSWF.toString(),
            targetSWF,
            catVictory,
            "/home/william/tmp/pita_icon.png"
    )
    logger.info {"Result: $res" }

    return
}
