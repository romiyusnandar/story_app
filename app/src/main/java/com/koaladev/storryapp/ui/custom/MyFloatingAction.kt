package com.koaladev.storryapp.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyFloatingAction(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout, child, directTargetChild, target, axes, type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed
        )

        if (dyConsumed > 0 && child.translationY == 0f) {
            child.animate()
                .translationY(child.height.toFloat() + child.marginBottom())
                .start()
        } else if (dyConsumed < 0 && child.translationY > 0f) {
            child.animate()
                .translationY(0f)
                .start()
        }
    }

    private fun View.marginBottom(): Int {
        val layoutParams = this.layoutParams
        return if (layoutParams is CoordinatorLayout.LayoutParams) {
            layoutParams.bottomMargin
        } else 0
    }
}
