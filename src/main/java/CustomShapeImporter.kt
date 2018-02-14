package dog.librewulf.teeoc

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.importers.ShapeImporter
import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag

/**
 * A custom subclass of the JPEXS image importer in order to resize an asset while still
 * maintaining the aspect ratio.
 */
class CustomShapeImporter: ShapeImporter() {
    @Override
    fun importImageMaintainAspect(st: ShapeTag, newData: ByteArray, tagType: Int): Tag {
        val imageTag = this.addImage(st, newData, tagType)
        st.setModified(true)

        // Calculate ratio to resize by. Mind the unit divisor!
        val rect = st.getRect()
        val dimension = imageTag.getImageDimension()
        val x_ratio = ((rect.Xmax - rect.Xmin) / SWF.unitDivisor) / dimension.getWidth()
        val y_ratio = ((rect.Ymax - rect.Ymin) / SWF.unitDivisor) / dimension.getHeight()
        val multiplier = Math.min(x_ratio, y_ratio)

        rect.Xmax = rect.Xmin + (SWF.unitDivisor * multiplier * dimension.getWidth()).toInt()
        rect.Ymax = rect.Ymin + (SWF.unitDivisor * multiplier * dimension.getHeight()).toInt()

        // TODO: Center the image, eventually

        val shapes = imageTag.getShape(rect, true)
        st.shapes = shapes

        return st
    }
}
