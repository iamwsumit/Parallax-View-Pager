package com.sumti1334.parallaxviewpager.ViewPager;

import android.animation.FloatEvaluator;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.viewpager.widget.ViewPager;

public class ParallaxTransformer implements ViewPager.PageTransformer {
    private Mode mMode;
    private Interpolator mInterpolator = new LinearInterpolator();
    private FloatEvaluator mEvaluator = new FloatEvaluator();
    private int mOutset;
    private float mOutsetFraction = 0.5F;

    public ParallaxTransformer() {
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public void transformPage(View page, float position) {
        page.setTranslationX(0.0F);
        if (position != 0.0F) {
            switch(this.mMode) {
                case LEFT_OVERLAY:
                    if (position > 0.0F) {
                        this.transform(page, position);
                    } else if (position < 0.0F) {
                        this.bringViewToFront(page);
                    }
                    break;
                case RIGHT_OVERLAY:
                    if (position < 0.0F) {
                        this.transform(page, position);
                    } else if (position > 0.0F) {
                        this.bringViewToFront(page);
                    }
                case NONE:
            }

        }
    }

    private void bringViewToFront(View view) {
        ViewGroup group = (ViewGroup)view.getParent();
        int index = group.indexOfChild(view);
        if (index != group.getChildCount() - 1) {
            view.bringToFront();
            if (VERSION.SDK_INT <= 19) {
                view.requestLayout();
                group.invalidate();
            }
        }

    }

    private void transform(View page, float position) {
        int pageWidth = page.getWidth();
        if (this.mOutset <= 0) {
            this.mOutset = (int)(this.mOutsetFraction * (float)page.getWidth());
        }

        float interpolatorPosition;
        float translationX;
        if (position < 0.0F) {
            interpolatorPosition = this.mInterpolator.getInterpolation(Math.abs(position));
            translationX = -this.mEvaluator.evaluate(interpolatorPosition, 0, pageWidth - this.mOutset);
        } else {
            interpolatorPosition = this.mInterpolator.getInterpolation(position);
            translationX = this.mEvaluator.evaluate(interpolatorPosition, 0, pageWidth - this.mOutset);
        }

        translationX += (float)(-page.getWidth()) * position;
        page.setTranslationX(translationX);
    }
}

