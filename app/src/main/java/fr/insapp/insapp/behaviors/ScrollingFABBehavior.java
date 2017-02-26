package fr.insapp.insapp.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by thoma on 22/12/2016.
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FrameLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FrameLayout fabContainer, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, fabContainer, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        if (fabContainer.getChildAt(0) instanceof FloatingActionMenu) {
            FloatingActionMenu fabMenu = (FloatingActionMenu) fabContainer.getChildAt(0);
            if (dyConsumed > 0) {
                fabMenu.hideMenuButton(true);
            } else if (dyConsumed < 0) {
                fabMenu.showMenuButton(true);
            }
        } else if (fabContainer.getChildAt(0) instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) fabContainer.getChildAt(0);
            if (dyConsumed > 5 && !fab.isHidden()) {
                fab.hide(true);
            } else if (dyConsumed <= 5 && fab.isHidden()) {
                fab.show(true);
            }
        }
    }
}