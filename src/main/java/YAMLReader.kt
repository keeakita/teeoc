import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.file.Files
import java.nio.file.Paths

object YAMLReader {
    fun read(resource: String): Map<String, SWFAssetInfo> {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.registerModule(KotlinModule())

        val path = this::class.java.classLoader.getResource(resource).path
        return Files.newBufferedReader(Paths.get(path)).use {
            mapper.readValue<Map<String, SWFAssetInfo>>(it)
        }
    }
}