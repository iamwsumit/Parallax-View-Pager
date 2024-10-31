package com.sumti1334.parallaxviewpager.ViewPager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import androidx.viewpager.widget.ViewPager;

public class ParallaxViewPager extends ViewPager {
    private Mode mMode;
    private int mShadowStart;
    private int mShadowMid;
    private int mShadowEnd;
    private Drawable mRightShadow;
    private Drawable mLeftShadow;
    private int mShadowWidth;
    private ParallaxTransformer mParallaxTransformer;
    private Interpolator mInterpolator;
    private int mOutset;
    private float mOutsetFraction;

    public ParallaxViewPager(Context context) {
        this(context, (AttributeSet)null);
    }

    public ParallaxViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShadowStart = Color.parseColor("#33000000");
        this.mShadowMid = Color.parseColor("#11000000");
        this.mShadowEnd = Color.parseColor("#00000000");
        this.mRightShadow = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{this.mShadowStart, this.mShadowMid, this.mShadowEnd});
        this.mLeftShadow = new GradientDrawable(Orientation.RIGHT_LEFT, new int[]{this.mShadowStart, this.mShadowMid, this.mShadowEnd});
        this.mOutsetFraction = 0.5F;
        this.mParallaxTransformer = new ParallaxTransformer();
        this.mMode = Mode.LEFT_OVERLAY;
        this.setMode(this.mMode);
        this.mShadowWidth = (int) this.dp2px(0,getContext());
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.drawShadow(canvas);
    }

    public void setInterpolator(Interpolator i) {
        this.mInterpolator = i;
        this.ensureInterpolator();
    }

    protected void ensureInterpolator() {
        if (this.mInterpolator == null) {
            this.mInterpolator = new LinearInterpolator();
        }

        if (this.mParallaxTransformer != null) {
            this.mParallaxTransformer.setInterpolator(this.mInterpolator);
        }

    }

    public void drawShadow(Canvas canvas) {
        if (this.mMode != Mode.NONE) {
            if (this.getScrollX() % this.getWidth() != 0) {
                switch(this.mMode) {
                    case LEFT_OVERLAY:
                        this.drawRightShadow(canvas);
                        break;
                    case RIGHT_OVERLAY:
                        this.drawLeftShadow(canvas);
                }

            }
        }
    }

    private void drawRightShadow(Canvas canvas) {
        canvas.save();
        float translate = (float)((this.getScrollX() / this.getWidth() + 1) * this.getWidth());
        canvas.translate(translate, 0.0F);
        this.mRightShadow.setBounds(0, 0, this.mShadowWidth, this.getHeight());
        this.mRightShadow.draw(canvas);
        canvas.restore();
    }

    private void drawLeftShadow(Canvas canvas) {
        canvas.save();
        float translate = (float)((this.getScrollX() / this.getWidth() + 1) * this.getWidth() - this.mShadowWidth);
        canvas.translate(translate, 0.0F);
        this.mLeftShadow.setBounds(0, 0, this.mShadowWidth, this.getHeight());
        this.mLeftShadow.draw(canvas);
        canvas.restore();
    }

    public void setPageMargin(int marginPixels) {
        super.setPageMargin((int) dp2px(marginPixels,getContext()));
    }

    private float dp2px(int dip, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (float)dip * scale + 0.5F;
    }

    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        if (offset == 0.0F) {
            int count = this.getChildCount();

            for(int i = 0; i < count; ++i) {
                this.mParallaxTransformer.transformPage(this.getChildAt(i), 0.0F);
            }
        }

    }

    public void setMode(Mode mode) {
        this.mMode = mode;
        this.mParallaxTransformer.setMode(mode);
        if (mode == Mode.LEFT_OVERLAY) {
            this.setPageTransformer(true, this.mParallaxTransformer);
        } else if (mode == Mode.RIGHT_OVERLAY) {
            this.setPageTransformer(false, this.mParallaxTransformer);
        }

    }
}

