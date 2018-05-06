package fr.insapp.insapp.utility
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class DarkenTransformation : BitmapTransformation() {

    companion object {
        private const val ID = "fr.insapp.insapp.utility.DarkenTransformation"
        private val ID_BYTES = ID.toByteArray()
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val canvas = Canvas(toTransform)
        val paint = Paint(Color.RED)
        val filter = LightingColorFilter(-0x454546, 0x00000000)

        paint.colorFilter = filter
        canvas.drawBitmap(toTransform, Matrix(), paint)

        return toTransform
    }

    override fun equals(other: Any?): Boolean = other is DarkenTransformation

    override fun hashCode(): Int =  ID.hashCode()

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }
}
