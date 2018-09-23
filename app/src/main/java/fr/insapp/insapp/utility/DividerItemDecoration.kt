package fr.insapp.insapp.utility

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView

/**
 * Created by thomas on 31/01/2017.
 */

class DividerItemDecoration(resources: Resources, drawable: Int) : RecyclerView.ItemDecoration() {

    private val divider: Drawable = resources.getDrawable(drawable)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}
