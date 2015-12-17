/*
 * ProgressPercent
 * Copyright (c) 2015  Natasa Misic
 *
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.natasa.progresspercent.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.natasa.progresspercent.R;
import com.natasa.progresspercent.lib.OnProgressTrackListener;


public class CircularProgress extends View {

    private static final int ANGLE_360 = 360;
    private Paint foregroundPaint, backgroundPaint;
    private float backgroundStrokeWidth = 3f, strokeWidth = 5f;
    private int PADDING = 20;
    private RectF rectF;
    private float bottom;
    private float right;
    private float top;
    private float left;
    private float angle;
    private int progress;
    private int maximum_progress = 100;
    private int height, width;
    private int min;
    private Paint textPaint;
    private int color = getResources().getColor(R.color.colorAccent), backgroundColor = Color.WHITE;
    private int textColor = getResources().getColor(R.color.colorPrimaryDark);
    private float angleX;
    private float angleY;
    private Context context;
    private final float ANGLE_180 = 180f;
    private final int angTxtMargin = 5;
    private final int startAngle0 = -80;
    private final int startAngle1 = -95;
    private int textSize = 26;
    private float startX, startY;
    private int circleTxtPadding;
    private int txtMarginX = 18;
    private int txtMarginY = 25;
    private int shadowColor = getResources().getColor(R.color.shader);
    private String typeface_path = "Roboto-Light.ttf";
    private boolean isRoundEdge;
    private boolean isShadowed;
    private OnProgressTrackListener listener;

    public void setOnProgressTrackListener(OnProgressTrackListener listener) {
        this.listener = listener;
    }

    public CircularProgress(Context context) {
        super(context);
        init(context);

    }

    public CircularProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        init(context);

    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Progress, 0, 0);
        try {
            progress = (int) typedArray.getFloat(
                    R.styleable.Progress_progress, progress);
            strokeWidth = typedArray.getDimension(
                    R.styleable.Progress_stroke_width, strokeWidth);
            backgroundStrokeWidth = typedArray.getDimension(
                    R.styleable.Progress_background_stroke_width,
                    backgroundStrokeWidth);
            color = typedArray.getInt(
                    R.styleable.Progress_progress_color, color);
            backgroundColor = typedArray.getInt(
                    R.styleable.Progress_background_color, backgroundColor);
            textColor = typedArray.getInt(
                    R.styleable.Progress_text_color, textColor);
            textSize = typedArray.getInt(
                    R.styleable.Progress_text_size, textSize);

        } finally {
            typedArray.recycle();
        }
    }

    private void init(Context ctx) {
        context = ctx;
        rectF = new RectF();
        setProgress(0);
        initBackgroundColor();
        initForegroundColor();
        initTextColor();
    }


    protected void initForegroundColor() {
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
        if (isRoundEdge) {
            foregroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        if (isShadowed) {
            this.setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint);
            foregroundPaint.setShadowLayer(4.0f, 0.0f, 2.0f, shadowColor);
        }
    }

    protected void initTextColor() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(1f);
        textPaint.setTextSize(textSize);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), typeface_path);
        textPaint.setTypeface(typeface);
    }

    protected void initBackgroundColor() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        if (isRoundEdge) {
            backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        if (isShadowed) {
            this.setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint);
            backgroundPaint.setShadowLayer(10.0f, 0.0f, 2.0f, shadowColor);
        }

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        angle = ANGLE_360 * progress / maximum_progress;

        canvas.drawArc(rectF, startAngle0, angle - ANGLE_360, false, backgroundPaint);
        if (progress > 1)
            canvas.drawArc(rectF, startAngle1, angle, false, foregroundPaint);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {

        angleX = (float) ((angle + 1) * Math.PI / ANGLE_180);
        angleY = (float) ((angle + 1) * Math.PI / ANGLE_180);

        startX = (float) (min / 2 - angTxtMargin + rectF.width() / 2 * Math.sin(angleX));
        startY = (float) (min / 2 + angTxtMargin - rectF.width() / 2 * Math.cos(angleY));


        if (progress > 98) {
            txtMarginX = 30;
            txtMarginY = -10;
            canvas.save();
            canvas.rotate(angle, startX, startY);

            canvas.drawText(String.format("%d%%", progress), startX - txtMarginX, startY + txtMarginY,
                    textPaint);
            canvas.restore();
        } else {
            canvas.drawText(String.format("%d%%", progress), startX - txtMarginX, startY + angTxtMargin,
                    textPaint);
        }
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        init(context);
    }

    public void setTypeface(String typefacePath) {
        this.typeface_path = typefacePath;
        init(context);

    }

    public int getProgressColor() {
        return color;
    }

    public void setProgressColor(int color) {
        this.color = color;
        init(context);

    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        init(context);

    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        init(context);

    }

    public void setProgressStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        init(context);
    }

    public void setBackgroundStrokeWidth(int strokeWidth) {
        this.backgroundStrokeWidth = strokeWidth;
        init(context);
    }

    public void setRoundEdge(boolean isRoundEdge) {
        this.isRoundEdge = isRoundEdge;
        init(context);
    }

    public void setShadow(boolean isShadowed) {
        this.isShadowed = isShadowed;
        init(context);

    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        min = setDimensions(widthMeasureSpec, heightMeasureSpec);
        circleTxtPadding = 30;

        left = 0 + strokeWidth / 2;
        top = 0 + strokeWidth / 2;
        right = min - strokeWidth / 2;
        bottom = min - strokeWidth / 2;
        rectF.set(left + PADDING + circleTxtPadding, top + PADDING + circleTxtPadding, right - PADDING - circleTxtPadding, bottom
                - PADDING - circleTxtPadding);
    }


    protected int setDimensions(int widthMeasureSpec, int heightMeasureSpec) {
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        final int smallerDimens = Math.min(width, height);

        setMeasuredDimension(smallerDimens, smallerDimens);
        return smallerDimens;
    }

    protected float getProgress() {
        return progress;
    }


    public void setProgress(int progress) {
        setProgressInView(progress);

    }

    private synchronized void setProgressInView(int progress) {
        this.progress = (progress <= maximum_progress) ? progress : maximum_progress;
        invalidate();
        trackProgressInView(progress);
    }

    private void trackProgressInView(int progress) {
        if (listener != null) {
            listener.onProgressUpdate(progress);
            if (progress >= maximum_progress) {
                listener.onProgressFinish();
            }
        }
    }

    public void resetProgress() {
        setProgress(0);

    }
}
