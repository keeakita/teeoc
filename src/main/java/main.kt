import com.jpexs.helpers.Helper
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

    val pack = ResourcePack("/home/william/tmp/teeoc-pack/pack.zip")
    pack.load(perSwfInfo)

    if (!pack.isLoaded) {
        logger.error { "Could not load pack ${pack.file_path}" }
        return
    }

    for ((entry, assetWithSWF) in pack.asset_ids) {
        // TODO: Iteration logic should probably be in the Swapper
        // TODO: Zip accessing logic should probably be done in the ResourcePack
        val (swfName, asset) = assetWithSWF
        val targetSWF = Paths.get(basePath, swfName).toString()
        logger.info { "SWF file path: ${targetSWF}" }
        logger.info { "Entry: ${entry}" }

        // Backup the original file to be safe
        val origPathObj = Paths.get(targetSWF + ".orig")
        if (!Files.exists(origPathObj)) {
            Files.copy(Paths.get(targetSWF), origPathObj)
        }

        val zipEntry = pack.zip.getEntry(entry)
        val zipEntryContents = Helper.readStream(pack.zip.getInputStream(zipEntry))

        val res = Swapper.replaceImageAsset(
                targetSWF,
                targetSWF,
                asset,
                zipEntryContents
        )
        logger.info {"Result: $res" }
    }

    return
}
