package com.mobillabb4.autosms.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Keep;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;

@Keep
public class MoveWithSnackbarBehavior extends CoordinatorLayout.Behavior<View> {

    public MoveWithSnackbarBehavior() {
        super();
    }

    public MoveWithSnackbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        return dependency instanceof Snackbar.SnackbarLayout;

    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float translationY = Math.min(0, ViewCompat.getTranslationY(dependency) - dependency.getHeight());

        //Dismiss last SnackBar immediately to prevent from conflict when showing SnackBars immediately after eachother
        ViewCompat.animate(child).cancel();

        //Move entire child layout up that causes objects on top disappear
        ViewCompat.setTranslationY(child, translationY);

        //Set top padding to child layout to reappear missing objects
        //If you had set padding to child in xml, then you have to set them here by <child.getPaddingLeft(), ...>
        child.setPadding(0, -Math.round(translationY), 0, 0);

        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {

        //Reset paddings and translationY to its default
        child.setPadding(0, 0, 0, 0);
        ViewCompat.animate(child).translationY(0).start();

    }
}