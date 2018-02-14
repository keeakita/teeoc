package dog.librewulf.teeoc

import mu.KotlinLogging
import org.apache.commons.lang3.SystemUtils

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Arrays
import java.util.regex.Pattern
import kotlin.streams.toList

/**
 * Locates Steam
 */
object SteamLocator {
    private val logger = KotlinLogging.logger {}

    /**
     * Attempts to find the path of Steam core files
     *
     * @return A string path to Steam, or null if it couldn't be
     * automatically found.
     */
    fun steamPath(): String? {
        if (SystemUtils.IS_OS_WINDOWS) {
            return winGetSteamPath()
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            return osxGetSteamPath()
        } else if (SystemUtils.IS_OS_LINUX) {
            return linuxGetSteamPath()
        } else {
            logger.warn { "Could not find SteamPath: Unrecognized OS ${SystemUtils.OS_NAME}" }
        }

        return null
    }

    private fun winGetSteamPath(): String? {
        val guessDir = getSteamPathFromRegistry()
        return if (File(guessDir).isDirectory) guessDir else null
    }

    private fun linuxGetSteamPath(): String? {
        val winePrefix = System.getenv("WINEPREFIX")
        if (winePrefix == null || winePrefix == "") {
            logger.info("WINEPREFIX environment variable unset, defaulting to ~/.wine")
        }

        val winDir = getSteamPathFromRegistry(true)

        // Convert the Windows style dir relative to the steampath
        val linuxDir = winDir
            ?.replace('\\', '/')
            ?.replace("C:", "drive_c", true)

        if (linuxDir == null) return null

        // The path might not be correct due to the registry key not having the right case.
        // (Windows has a case insensitive filesystem, Linux does not)
        val guessDir = resolvePathCase(Paths.get(winePrefix), Paths.get(linuxDir)).toString()

        return if (File(guessDir).isDirectory) guessDir else null
    }

    private fun osxGetSteamPath(): String? {
        val guessDir = "${SystemUtils.USER_HOME}/Library/Application Support/Steam/steamapps"
        return if (File(guessDir).isDirectory) guessDir else null
    }

    /**
     * Parses out the SteamPath from the output of the registry command
     *
     * @param stream the stream from the process
     * @return the SteamPath, or null if not found
     */
    private fun parseRegOutput(stream: InputStreamReader): String? {
        val input = BufferedReader(stream)
        val regKeyExtractor = Pattern.compile("\\s+SteamPath\\s+REG_SZ\\s+(.+)$")

        return input.lines().toList().map {
            val match = regKeyExtractor.matcher(it)
            if (match.find()) {
                return@map match.group(1)
            }
            return@map null
        }.firstOrNull { it != null }
    }

    private fun getSteamPathFromRegistry(useWine: Boolean = false): String? {
        var path: String? = null

        val regCommand = mutableListOf(
                "reg",
                "query",
                "HKEY_CURRENT_USER\\SOFTWARE\\Valve\\Steam",
                "/v",
                "SteamPath"
        )
        if (useWine) {
            regCommand.add(0, "wine")
        }

        try {
            val reg = ProcessBuilder(regCommand).start()
            val procOutput = InputStreamReader(reg.inputStream)
            reg.waitFor()
            path = parseRegOutput(procOutput)
        } catch (ioe: IOException) {
            logger.warn("Exception reading the registry:")
            logger.warn(Arrays.toString(ioe.stackTrace))
        } catch (ie: InterruptedException) {
            logger.warn("Registry read process was interrupted:")
            logger.warn(Arrays.toString(ie.stackTrace))
        }

        return path
    }

    /**
     * Searches the directory `prefix` for a case-insensitive match to `suffix`.
     */
    private fun resolvePathCase(prefix: Path, suffix: Path): Path? {
        if (prefix.toFile().isDirectory) {
            val contents = prefix.toFile().list()
            val target = suffix.subpath(0, 1).toString()

            val match = contents.firstOrNull() {
                it.toLowerCase() == target.toLowerCase()
            }

            if (match != null) {
                if (suffix.nameCount == 1) {
                    return prefix.resolve(match)
                }

                return resolvePathCase(
                    prefix.resolve(match),
                    suffix.subpath(1, suffix.nameCount)
                )
            }
        }

        return null
    }
}
