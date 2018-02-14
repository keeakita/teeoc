/**
 * Represents a collection of info about what assets a SWF contains.
 */
data class SWFAssetInfo(
        val file: String,
        val visual_assets: Map<String, Map<String, Int>>?,
        val animation_assets: Map<String, Map<String, List<Int>>>?,
        val audio_assets: Map<String, Map<String, Int>>?
)
