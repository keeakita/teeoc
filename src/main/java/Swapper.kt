package dog.librewulf.teeoc

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.configuration.Configuration
import com.jpexs.decompiler.flash.importers.ImageImporter
import com.jpexs.decompiler.flash.importers.ShapeImporter
import com.jpexs.decompiler.flash.tags.base.*
import com.jpexs.helpers.Helper
import mu.KotlinLogging

import java.io.*

object Swapper {
    private val logger = KotlinLogging.logger {}

    /**
     * Creates a copy of `sourceSwf` named `destSwf` with the asset located at `characterId`
     * replaced with the image located at `replacement`.
     */
    fun replaceImageAsset(
        sourceSwf: String,
        destSwf: String,
        characterId: Int,
        replacement: String
    ): Boolean {
        val inFile = File(sourceSwf)
        val outFile = File(destSwf)
        try {
            val inStream = FileInputStream(inFile)
            val swf = SWF(inStream, Configuration.parallelSpeedUp.get())

            if (!swf.getCharacters().containsKey(characterId)) {
                // TODO: Should this be an exception?
                logger.error { "CharacterId does not exist" }
                return false
            }

            val characterTag = swf.getCharacter(characterId)
            val data = Helper.readFile(replacement)
            if (characterTag is ImageTag) {
                ImageImporter().importImage(characterTag, data, 0)
            } else if (characterTag is ShapeTag) {
                ShapeImporter().importImage(characterTag, data, 0, false)
            }

            BufferedOutputStream(FileOutputStream(outFile)).use({ fos -> swf.saveTo(fos) })
        } catch (e: IOException) {
            logger.error { "I/O error during SWF asset replacing" }
            return false;
        } catch (e: InterruptedException) {
            logger.error { "I/O error during SWF asset replacing" }
            return false;
        }

        return true;
    }
}
