package com.z.zpicselect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/*
 * 正方形的FrameLayout容器
 */
public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(Context context) {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 自定义View
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 设置宽高为一样
        setMeasuredDimension(width, width);
    }
}
