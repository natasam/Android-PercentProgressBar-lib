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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.natasa.progresspercent.R;

public class LineProgress extends View {

    private Paint foregroundPaint, backgroundPaint;
    private int color = getResources().getColor(R.color.colorPrimary);
    private int backgroundColor = Color.WHITE;
    private float backgroundStrokeWidth = 5f, strokeWidth = 5f;
    private int PADDING = 20;

    private float progress;
    private int maximum_progress = 100;
    private int height, width;
    private Paint thumbPaint;
    private Paint textPaint;
    private int textColor = getResources().getColor(R.color.colorAccent);
    private RectF rectP;
    private int shadowColor = getResources().getColor(R.color.shader);
    private String typeface_path = "Roboto-Light.ttf";
    private boolean isRoundEdge;
    private boolean isShadowed;
    private Context context;
    private int textSize = 26;
    private OnProgressTrackListener listener;

    public void setOnProgressTrackListener(OnProgressTrackListener listener) {
        this.listener = listener;
    }

    public LineProgress(Context context) {
        super(context);
        init(context);

    }

    public LineProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        init(context);

    }

    public LineProgress(Context context, AttributeSet attrs, int defStyle) {
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

    private void init(Context context) {
        this.context = context;
        progress = 0;

        initBackgroundColor();
        initForegroundColor();
        initTextColor();
        rectP = new RectF();

    }

    protected void initForegroundColor() {
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
        //if (isRoundEdge) {
        foregroundPaint.setStrokeCap(Paint.Cap.ROUND);
        this.setLayerType(LAYER_TYPE_SOFTWARE, foregroundPaint);

        if (isShadowed) {

            foregroundPaint.setShadowLayer(3.0f, 0.0f, 2.0f, Color.parseColor("#33000000"));
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
        this.setLayerType(LAYER_TYPE_SOFTWARE, backgroundPaint);

        if (isShadowed) {

            backgroundPaint.setShadowLayer(3.0f, 0.0f, 2.0f, Color.parseColor("#33000000"));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRects2(canvas);


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

    private void drawRects2(Canvas canvas) {
        int nMiddle = height / 2;
        Rect bounds = new Rect();
        String text = "" + (int) progress + "%";
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        float mt = textPaint.measureText(text) + 40 + text.length();

        int progressX = (int) ((width - mt) * progress / maximum_progress);

        rectP.left = getPaddingLeft();
        rectP.top = nMiddle;
        rectP.right = progressX;
        rectP.bottom = nMiddle;
        if (progress > 2)
            canvas.drawRect(rectP, foregroundPaint);


        if (progress < maximum_progress)
            canvas.drawRect(rectP.width() + mt, nMiddle, width - getPaddingRight(), nMiddle, backgroundPaint);

        canvas.drawText(text, progressX + 10, nMiddle + backgroundStrokeWidth,
                textPaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        width = (int) (w - xpad);
        height = (int) (h - ypad);
        setMeasuredDimension(width, height);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPadding(PADDING, 0, 0, 0);


    }


    protected float getProgress() {
        return progress;
    }


    public void setProgress(int progress) {
        setProgressInView(progress);
    }

    private void setProgressInView(int progress) {
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
