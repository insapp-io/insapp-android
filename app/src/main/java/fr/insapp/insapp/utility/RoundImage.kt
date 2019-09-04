package fr.insapp.insapp.utility

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log

class RoundImage {

    companion object {
        fun createRoundDrawable(resources: Resources, profileID: Int): Drawable {
            val sourceBitmap = BitmapFactory.decodeResource(resources, profileID)
            return BitmapDrawable(resources, createCircleBitmap(sourceBitmap))
        }

        private fun createCircleBitmap(bitmapimg: Bitmap): Bitmap {
            val output = Bitmap.createBitmap(bitmapimg.width, bitmapimg.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val rect = Rect(0, 0, bitmapimg.width, bitmapimg.height)

            val paint = Paint()
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = 0xff424242.toInt()
            canvas.drawCircle(bitmapimg.width / 2f,bitmapimg.height / 2f, bitmapimg.width / 2f - 4f, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmapimg, rect, rect, paint)
            return output
        }
    }
}