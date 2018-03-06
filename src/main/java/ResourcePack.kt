import mu.KotlinLogging
import java.util.zip.ZipFile

class ResourcePack(val file_path: String) {
    private val logger = KotlinLogging.logger {}

    // Maps an entry in the zip file to the name of the SWF and ID of the asset
    val asset_ids: MutableMap<String, Pair<String, Int>> = mutableMapOf()
    val zip  = ZipFile(this.file_path)
    var isLoaded = false
        private set

    private fun entry_to_asset(
        name: String,
        info: Map<String, SWFAssetInfo>
    ): Pair<String, Int>? {
        val parts = name.split("/")

        if (parts.size != 4) {
            logger.error { "Zipfile entries should be 4 levels deep: $name" }
            return null
        }

        val swf = info[parts[0]]
        if (swf == null) {
            logger.error { "Top level directory must be a SWF identifier: $name" }
            return null
        }

        // TODO: animation_assets? This will probably fuck up the types.
        val asset_groups: Map<String, Map<String, Int>>
        if (parts[1] == "visual_assets" && swf.visual_assets != null) {
            asset_groups = swf.visual_assets
        } else if (parts[1] == "audio-assets" && swf.audio_assets != null) {
            asset_groups = swf.audio_assets
        } else {
            logger.error { "2nd level directory must be the asset type: $name" }
            return null
        }

        val asset_group = asset_groups[parts[2]]
        if (asset_group == null) {
            logger.error { "3rd level directory must be the asset group: $name" }
            return null
        }

        // The last part is a character name, with some file extension. Consider the first period
        // the denote the extension.
        val character = parts[3].split(".")[0]
        val asset_id = asset_group[character]
        if(asset_id == null) {
            logger.error { "4th level directory must be the character name: $name" }
            return null
        }

        return Pair(swf.file, asset_id)
    }

    fun load(info: Map<String, SWFAssetInfo>): Boolean {
        for (entry in zip.entries()) {
            if (!entry.isDirectory) {
                val asset = entry_to_asset(entry.name, info)
                if (asset == null) {
                    logger.error { "Resource pack is not valid." }
                    asset_ids.clear()
                    return false
                }

                asset_ids[entry.name] = asset
            }
        }

        this.isLoaded = true
        return true
    }
}
