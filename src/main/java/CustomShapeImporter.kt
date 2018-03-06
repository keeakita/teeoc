package dog.librewulf.teeoc

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.importers.ShapeImporter
import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag

/**
 * A custom subclass of the JPEXS image importer in order to resize an asset while tweaking the
 * aspect ratio.
 */
class CustomShapeImporter: ShapeImporter() {
    @Override

    /**
     * Imports an image, forcing the target shape to be a square with the size of the maximum
     * dimension of the target.
     */
    fun importImageForceSquare(st: ShapeTag, newData: ByteArray, tagType: Int): Tag {
        val imageTag = this.addImage(st, newData, tagType)
        val rect = st.getRect()
        st.setModified(true)

        // Create a new square with the same center point
        val maxDimension = Math.max(rect.width, rect.height)
        val centerX = rect.Xmin + (rect.width / 2)
        val centerY = rect.Ymin + (rect.height / 2)
        rect.Xmin = centerX - (maxDimension / 2)
        rect.Xmax = centerX + (maxDimension / 2)
        rect.Ymin = centerY - (maxDimension / 2)
        rect.Ymax = centerY + (maxDimension / 2)

        // Resize image, mantaining aspect. Mind the unit divisor!
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
